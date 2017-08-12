package com.allen.activitiDemo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class LineTest {
	/**
	 * 获取默认流程引擎实例，会自动读取activiti.cfg.xml文件
	 */
	private ProcessEngine processEngine = ProcessEngines
			.getDefaultProcessEngine();

	
	/**
     * 部署流程定义
     */
    @Test
    public void deploy(){
        Deployment deployment=processEngine.getRepositoryService() // 获取部署相关Service
                .createDeployment() // 创建部署
                .addClasspathResource("diagrams/StudentLeave2Process.bpmn") // 加载资源文件
                .addClasspathResource("diagrams/StudentLeave2Process.png") // 加载资源文件
                .name("学生特殊请假流程") // 流程名称
                .deploy(); // 部署
        System.out.println("流程部署ID:"+deployment.getId()); 
        System.out.println("流程部署Name:"+deployment.getName());
    }
	
    
    /**
     * 启动流程实例
     */
    @Test
    public void start(){
        ProcessInstance pi=processEngine.getRuntimeService() // 运行时Service
            .startProcessInstanceByKey("studentLeave2Process"); // 流程定义表的KEY字段值
        System.out.println("流程实例ID:"+pi.getId());
        System.out.println("流程定义ID:"+pi.getProcessDefinitionId()); 
    }
     
    /**
     * 查看任务
     */
    @Test
    public void findTask(){
        List<Task> taskList=processEngine.getTaskService() // 任务相关Service
            .createTaskQuery() // 创建任务查询
            .taskAssignee("李四") // 指定某个人
            .list();
        for(Task task:taskList){
            System.out.println("任务ID:"+task.getId()); 
            System.out.println("任务名称:"+task.getName());
            System.out.println("任务创建时间:"+task.getCreateTime());
            System.out.println("任务委派人:"+task.getAssignee());
            System.out.println("流程实例ID:"+task.getProcessInstanceId());
        }
    }
    
    /**
     * 完成任务
     */
    @Test
    public void completeTask(){
        processEngine.getTaskService() // 任务相关Service
            .complete("57504");
    }
     
    @Test
    public void completeTask2(){
 
        Map<String, Object> variables=new HashMap<String,Object>();
        variables.put("msg", "特殊情况");//特殊情况需要老师进行审批，需要执行completeTask3
        //variables.put("msg", "一般情况");//不需要老师进行审批，执行完此方法审批流程结束
        processEngine.getTaskService() // 任务相关Service
            .complete("60002", variables); //完成任务的时候，设置流程变量
    }
    @Test
    public void completeTask3(){
    	processEngine.getTaskService() // 任务相关Service
    	.complete("62506"); //完成任务的时候，设置流程变量
    }
    
    
}
