package com.hdu.language_learning_system.task.service.impl;


import com.hdu.language_learning_system.course.entity.Schedule;
import com.hdu.language_learning_system.course.repository.StudentScheduleRecordRepository;
import com.hdu.language_learning_system.course.repository.ScheduleRepository;
import com.hdu.language_learning_system.notification.entity.Notification;
import com.hdu.language_learning_system.task.dto.TaskDetailDTO;
import com.hdu.language_learning_system.task.dto.TaskPublishDTO;
import com.hdu.language_learning_system.task.entity.Task;
import com.hdu.language_learning_system.task.entity.TaskAssignment;
import com.hdu.language_learning_system.task.dto.TaskListDTO;
import com.hdu.language_learning_system.notification.repository.NotificationRepository;
import java.util.ArrayList;
import java.util.List;
import com.hdu.language_learning_system.task.repository.TaskRepository;
import com.hdu.language_learning_system.task.repository.TaskAssignmentRepository;
import com.hdu.language_learning_system.task.service.TaskService;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private ScheduleRepository scheduleRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private TaskAssignmentRepository taskAssignmentRepository;
    @Resource
    private StudentScheduleRecordRepository studentScheduleRecordRepository;

    @Resource
    private NotificationRepository notificationRepository;

    //任务发布（课后任务或者学员个人任务）
    @Override
    public void publishTask(TaskPublishDTO dto) {
        Task task = new Task();

        if ("课后作业".equals(dto.getTaskType())) {
            if (dto.getScheduleId() == null) {
                throw new IllegalArgumentException("课后作业必须绑定课程 scheduleId");
            }
            task.setSchedule(scheduleRepository.findById(dto.getScheduleId())
                    .orElseThrow(() -> new RuntimeException("课表不存在")));
            task.setStudent(null); // 课后作业不给学生字段
        } else if ("训练任务".equals(dto.getTaskType())) {
            if (dto.getStudentId() == null) {
                throw new IllegalArgumentException("训练任务必须指定 studentId");
            }
            task.setStudent(userRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new RuntimeException("学生不存在")));
            task.setSchedule(null); // 不绑定 schedule
        } else {
            throw new IllegalArgumentException("未知任务类型: " + dto.getTaskType());
        }

        task.setPublisher(userRepository.findById(dto.getPublisherId())
                .orElseThrow(() -> new RuntimeException("发布者不存在")));
        task.setTaskType(dto.getTaskType());
        task.setTaskContent(dto.getTaskContent());
        task.setPublishTime(new Timestamp(System.currentTimeMillis()));
        task.setDeadline(dto.getDeadline());  // 设置任务截止时间

        taskRepository.save(task);

        // 如果是课后作业，还需要生成所有学生的 TaskAssignment
        if ("课后作业".equals(dto.getTaskType())) {
            List<User> students = studentScheduleRecordRepository
                    .findStudentsByScheduleId(dto.getScheduleId());

            for (User student : students) {
                TaskAssignment assignment = new TaskAssignment();
                assignment.setTask(task);
                assignment.setStudent(student);
                assignment.setCompletionStatus("未完成");
                taskAssignmentRepository.save(assignment);
            }
        }

        // 训练任务自动只生成一个 assignment
        if ("训练任务".equals(dto.getTaskType())) {
            TaskAssignment assignment = new TaskAssignment();
            assignment.setTask(task);
            assignment.setStudent(task.getStudent());
            assignment.setCompletionStatus("未完成");
            taskAssignmentRepository.save(assignment);
        }
        // 发布成功后发送通知
        Notification notification = new Notification();
        notification.setNotificationType("任务通知");
        notification.setRefTargetId(task.getTaskId());               // 引用任务ID
        notification.setRefTargetType("任务");                        // 引用类型
        notification.setContent("您有新的" + dto.getTaskType() + "，请及时完成！");
        notification.setSentTime(new Timestamp(System.currentTimeMillis()));
        notification.setStatus("未读");

// 根据类型设置通知接收人
        if ("课后作业".equals(dto.getTaskType())) {
            // 获取该 schedule 下的所有学员
            List<User> students = studentScheduleRecordRepository.findStudentsByScheduleId(dto.getScheduleId());
            for (User s : students) {
                Notification clone = new Notification(notification);
                clone.setReceiver(s);
                notificationRepository.save(clone);
            }
        } else if ("训练任务".equals(dto.getTaskType())) {
            User s = userRepository.findById(dto.getStudentId()).orElseThrow();
            notification.setReceiver(s);
            notificationRepository.save(notification);
        }
    }

    //学员课后任务清单查看
    @Override
    public List<TaskListDTO> getTaskListByStudentId(Integer studentId) {
        List<TaskAssignment> assignments = taskAssignmentRepository.findByStudent_UserId(studentId);
        List<TaskListDTO> result = new ArrayList<>();

        for (TaskAssignment a : assignments) {
            TaskListDTO dto = new TaskListDTO();
            dto.setTaskId(a.getTask().getTaskId());
            dto.setTaskType(a.getTask().getTaskType());
            dto.setTaskContent(a.getTask().getTaskContent());
            dto.setPublishTime(a.getTask().getPublishTime());
            dto.setDeadline(a.getTask().getDeadline()); // 新增字段
            dto.setCompletionStatus(a.getCompletionStatus());
//            dto.setSubmitTime(a.getSubmitTime());
//            dto.setAttachmentPath(a.getAttachmentPath());
//            dto.setScore(a.getScore());
//            dto.setGradeComment(a.getGradeComment());
            result.add(dto);
        }

        return result;
    }
    //查看任务详情
    @Override
    public TaskDetailDTO getTaskDetail(Integer taskId, Integer studentId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        TaskDetailDTO dto = new TaskDetailDTO();
        dto.setTaskId(task.getTaskId());
        dto.setTaskType(task.getTaskType());
        dto.setTaskContent(task.getTaskContent());
        dto.setPublishTime(task.getPublishTime());
        dto.setDeadline(task.getDeadline());

        // 如果提供了 studentId，则查找对应提交信息
        if (studentId != null) {
            taskAssignmentRepository
                    .findByTask_TaskIdAndStudent_UserId(taskId, studentId)
                    .ifPresent(a -> {
                        dto.setCompletionStatus(a.getCompletionStatus());
                        dto.setSubmitTime(a.getSubmitTime());
                        dto.setSubmitText(a.getSubmitText());
                        dto.setAttachmentPath(a.getAttachmentPath());
                        dto.setScore(a.getScore());
                        dto.setGradeComment(a.getGradeComment());
                    });
        }

        return dto;
    }
    //助教查看自己发布的任务
    @Override
    public List<TaskListDTO> getTasksPublishedBy(Integer publisherId) {
        List<Task> tasks = taskRepository.findByPublisher_UserId(publisherId);
        List<TaskListDTO> result = new ArrayList<>();

        for (Task task : tasks) {
            TaskListDTO dto = new TaskListDTO();
            dto.setTaskId(task.getTaskId());
            dto.setTaskType(task.getTaskType());
            dto.setTaskContent(task.getTaskContent());
            dto.setPublishTime(task.getPublishTime());
            dto.setDeadline(task.getDeadline());
            result.add(dto);
        }

        return result;
    }
}