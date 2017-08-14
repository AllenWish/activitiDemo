package com.allen.groupAssign;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.junit.Test;

/**
 * Activiti提供了一个Service来专门操作用户组表，那就是 IdentityService 身份信息Service
 * 我们可以用过IdentityService来添加修改用户信息，组信息，也可以删除用户信息，组信息，以及维护他们的关联关系；
 * @author CGy-Allenwish
 *
 */
public class IdentityTest {
	/**
	 * 获取默认流程引擎实例，会自动读取activiti.cfg.xml
	 */
	private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	@Test
	public void testSaveUser(){
		IdentityService identityService = processEngine.getIdentityService();
		User user = new UserEntity();
		user.setId("allen");
		user.setEmail("e@qq.com");
		user.setPassword("123456");
		identityService.saveUser(user);
	}
	
	/**
     * 测试删除用户
     */
    @Test
    public void testDeleteUser(){
        IdentityService identityService=processEngine.getIdentityService();
        identityService.deleteUser("zhangsan"); 
    }
    /**
     * 测试添加组
     */
    @Test
    public void testSaveGroup(){
        IdentityService identityService=processEngine.getIdentityService();
        Group group=new GroupEntity(); // 实例化组实体
        group.setId("test");
        identityService.saveGroup(group);
    }
    /**
     * 测试删除组
     */
    @Test
    public void testDeleteGroup(){
        IdentityService identityService=processEngine.getIdentityService();
        identityService.deleteGroup("test");
    }
     
    /**
     * 测试添加用户和组关联关系
     */
    @Test
    public void testSaveMembership(){
        IdentityService identityService=processEngine.getIdentityService();
        identityService.createMembership("allen", "test");
    }
     
    /**
     * 测试删除用户和组关联关系
     */
    @Test
    public void testDeleteMembership(){
        IdentityService identityService=processEngine.getIdentityService();
        identityService.deleteMembership("allen", "test");
    }
}

