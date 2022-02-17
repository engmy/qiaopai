package com.qixuan.admin.service.impl;

import com.qixuan.admin.service.RoleMenuService;
import com.qixuan.common.entity.Admin;
import com.qixuan.common.entity.Menu;
import com.qixuan.common.entity.RoleMenu;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleMenuServiceImpl implements RoleMenuService
{
    @Resource
    private HttpSession session;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean insertRoleMenu(String roleId, String menuIds)
    {
        Admin admin = (Admin) session.getAttribute("admin");
        List<RoleMenu> roleMenuList = new ArrayList();

        for (String menuId : menuIds.split(","))
        {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);
            roleMenu.setCreateUser(admin.getUsername());
            roleMenu.setUpdateUser(admin.getUsername());
            roleMenuList.add(roleMenu);
        }
        Collection<RoleMenu> roleMenuResult = mongoTemplate.insert(roleMenuList, RoleMenu.class);
        if(roleMenuResult.size()>0)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public List<RoleMenu> getRoleMenuList(String roleId)
    {
        Query query = Query.query(Criteria.where("role_id").is(roleId));
        return  mongoTemplate.find(query, RoleMenu.class);
    }

    @Override
    public Boolean deleteRoleMenu(String roleId)
    {
        Query query = Query.query(Criteria.where("role_id").is(roleId));
        Long count = mongoTemplate.remove(query, RoleMenu.class).getDeletedCount();
        if(count>0)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Set<String> getRoleMenuIds(String roleId, Boolean parent)
    {
        Set<String> menuIdArr = new HashSet<>();
        List<RoleMenu> roleMenuList = this.getRoleMenuList(roleId);

        Map<String, Set<String>> menuIds = roleMenuList.stream().collect(Collectors.groupingBy(RoleMenu::getRoleId, Collectors.mapping(RoleMenu::getMenuId, Collectors.toSet())));
        if(menuIds.size()>0)
        {
            menuIdArr = menuIds.get(roleId);
            if(!parent)
            {
                Set<String> newmenuIdArr = new HashSet<>();
                for (String menuId : menuIdArr)
                {
                    Query query = new Query(Criteria.where("parent_id").in(menuId));
                    Menu menu = mongoTemplate.findOne(query, Menu.class);
                    if(menu==null) {
                        newmenuIdArr.add(menuId);
                    }
                }
                menuIdArr = newmenuIdArr;
            }
        }
        return menuIdArr;
    }

    @Override
    public Set<String> getRoleMenuNames(String roleId)
    {
        Set<String> menuNames = new HashSet<>();
        Set<String> menuIds = this.getRoleMenuIds(roleId, true);
        if(menuIds.size()>0)
        {
            Query query = new Query(Criteria.where("_id").in(menuIds));
            List<Menu> menuList = mongoTemplate.find(query, Menu.class);
            for (Menu menu: menuList)
            {
                menuNames.add(menu.getMenuName());
            }
        }
        return menuNames;
    }

    @Override
    public List<String> getRoleMenuPermissions(String roleId)
    {
        List<String> permissions = new ArrayList();
        Set<String> menuIds = this.getRoleMenuIds(roleId, true);
        if(menuIds.size()>0)
        {
            Query query = new Query(Criteria.where("_id").in(menuIds));
            List<Menu> menuList = mongoTemplate.find(query, Menu.class);
            for (Menu menu: menuList)
            {
                permissions.add(menu.getPermission());
            }
        }
        return permissions;
    }
}