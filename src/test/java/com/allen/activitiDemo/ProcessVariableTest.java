package com.allen.activitiDemo;

import java.util.Date;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

import com.allen.activitiEntity.Student;
/**
 * 学生请假流程
 * 学生提交请假申请，辅导员审批通过
 * @author cgy-allenwish
 *
 */
public class ProcessVariableTest {
 
    /**
     * 获取默认的流程引擎实例 会自动读取activiti.cfg.xml文件 
     */
    private ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
     
    /**
     * 部署流程定义
     */
    @Test
    public void deploy(){
        // 获取部署对象
        Deployment deployment=processEngine.getRepositoryService() // 部署Service
                     .createDeployment()  // 创建部署
                     .addClasspathResource("diagrams/StudentLeaveProcess.bpmn")  // 加载资源文件
                     .addClasspathResource("diagrams/StudentLeaveProcess.png")   // 加载资源文件
                     .name("学生请假流程")  // 流程名称
                     .deploy(); // 部署
        System.out.println("流程部署ID:"+deployment.getId());
        System.out.println("流程部署Name:"+deployment.getName());
    }
    
    /**
     * 启动流程实例
     */
    @Test
    public void start(){
        // 启动并获取流程实例
        ProcessInstance processInstance=processEngine.getRuntimeService() // 运行时流程实例Service
            .startProcessInstanceByKey("studentLeaveProcess"); // 流程定义表的KEY字段值
        System.out.println("流程实例ID:"+processInstance.getId());
        System.out.println("流程定义ID:"+processInstance.getProcessDefinitionId());
    }
    
    
    /**
     * 设置流程变量以及值
     */
    @Test
    public void setVariablesValues(){
        TaskService taskService=processEngine.getTaskService(); // 任务Service
        String taskId="35004"; // 任务id
        taskService.setVariableLocal(taskId, "days", 2); // 存Integer类型
        taskService.setVariable(taskId, "date", new Date()); // 存日期类型
        taskService.setVariable(taskId, "reason", "发烧"); // 存字符串
        Student student=new Student();
        student.setId(1);
        student.setName("张三");
        taskService.setVariable(taskId, "student", student);  // 存序列化对象
    }
    
    
    /**
     * 完成任务
     * 此时执行完请假，到审批人进行审批节点
     */
    @Test
    public void completeTask(){
        processEngine.getTaskService() // 任务相关Service
                .complete("35004"); // 指定要完成的任务ID
    }
    
    
    /**
     * 获取流程变量以及值
     * 
     * 这里无法获取到days是因为上面我们设置days的时候用的是setVariableLocal任务节点本地变量
     * 只有在前面那个节点作用域内有效，所以在这个节点是取不到的；
     */
    @Test
    public void getVariablesValue(){
        TaskService taskService=processEngine.getTaskService(); // 任务Service
        String taskId="40002"; // 任务id
        Integer days=(Integer) taskService.getVariable(taskId, "days");
        Date date=(Date) taskService.getVariable(taskId, "date");
        String reason=(String) taskService.getVariable(taskId, "reason");
        Student student=(Student) taskService.getVariable(taskId, "student");
        System.out.println("请假天数："+days);
        System.out.println("请假日期："+date);
        System.out.println("请假原因："+reason);
        System.out.println("请假对象："+student.getId()+","+student.getName());
    }
    /**
     * 完成任务
     * 这里是辅导员进行审批通过
     */
    @Test
    public void completeTask2(){
        processEngine.getTaskService() // 任务相关Service
                .complete("40002"); // 指定要完成的任务ID
    }
    
}
