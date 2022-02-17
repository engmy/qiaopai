package com.qixuan.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mongodb.client.result.UpdateResult;
import com.qixuan.admin.form.MenuForm;
import com.qixuan.admin.service.MenuService;
import com.qixuan.admin.service.RoleMenuService;
import com.qixuan.common.entity.Admin;
import com.qixuan.common.entity.Menu;
import com.qixuan.common.exception.CustomException;
import com.qixuan.common.utils.MongoUtil;
import com.qixuan.common.utils.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService
{
    @Resource
    private HttpSession session;

    @Resource
    private HttpServletRequest request;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private RoleMenuService roleMenuService;

    @Override
    public PageHelper getMenuList(Integer page, Integer limit, Map<String, String> params)
    {
        Criteria criteria = Criteria.where("_id").ne("");
        // 菜单查询
        if(StrUtil.isNotEmpty(params.get("menu_name")))
        {
            criteria.and("menu_name").regex(params.get("menu_name"));
        }
        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Order.desc("_id")));
        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.start(page, limit, query);
        List<Menu> menuList = mongoTemplate.find(query, Menu.class);
        Long count = mongoTemplate.count(new Query(criteria), Menu.class);
        return mongoUtil.pageHelper(count, menuList);
    }

    @Override
    public Boolean insertMenu(MenuForm menuForm)
    {
        Admin admin = (Admin) session.getAttribute("admin");

        if (this.existMenuUrl(menuForm.getUrl()))
        {
            throw new CustomException("菜单路劲已存在");
        }
        if (this.existMenuPermission(menuForm.getPermission()))
        {
            throw new CustomException("权限标识已存在");
        }
        Menu menu = new Menu();
        BeanUtils.copyProperties(menuForm, menu);
        menu.setCreateUser(admin.getUsername());
        menu.setUpdateUser(admin.getUsername());
        Menu roleResult = mongoTemplate.insert(menu);

        if(roleResult!=null)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean updateMenu(MenuForm menuForm)
    {
        Admin admin = (Admin) session.getAttribute("admin");

        Query query = Query.query(Criteria.where("_id").is(menuForm.getMenuId()));
        Update update = new Update();
        update.set("menu_name",    menuForm.getMenuName());
        update.set("parent_id",    menuForm.getParentId());
        update.set("url",          menuForm.getUrl());
        update.set("icon",         menuForm.getIcon());
        update.set("sort",         menuForm.getSort());
        update.set("url_type",     menuForm.getUrlType());
        update.set("remarks",      menuForm.getRemarks());
        update.set("permission",   menuForm.getPermission());
        update.set("update_user",  admin.getUsername());
		update.set("update_time",  DateUtil.date());
        UpdateResult menuResult = mongoTemplate.updateFirst(query, update, Menu.class);
        if(menuResult!=null)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean deleteMenu(String menuId)
    {
        if(this.existMenuChildren(menuId))
        {
            throw new CustomException("该菜单有下级菜单不能被删除");
        }

        Query query = Query.query(Criteria.where("_id").is(menuId));
        Long count = mongoTemplate.remove(query, Menu.class).getDeletedCount();
        if(count>0) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Menu getMenuInfo(String menuId)
    {
        return mongoTemplate.findById(menuId, Menu.class);
    }

    @Override
    public List<Tree<String>> getTreeMenuList()
    {
        List<Menu> menuList = mongoTemplate.findAll(Menu.class);
        List<TreeNode<String>> nodeList = CollUtil.newArrayList();
        for (Menu menu: menuList)
        {
            HashMap map = new HashMap();
            map.put("spread", true);
            map.put("title",  menu.getMenuName());
            nodeList.add(new TreeNode<>(menu.getMenuId(), menu.getParentId(), menu.getMenuName(), menu.getSort()).setExtra(map));
        }
        List<Tree<String>> treeList = TreeUtil.build(nodeList, "0");
        return treeList;
    }

    @Override
    public List<Tree<String>> getTreeRoleMenuList(String roleId)
    {
        Query query = new Query(Criteria.where("_id").in(roleMenuService.getRoleMenuIds(roleId, true)));
        query.with(Sort.by(Sort.Order.desc("sort")));
        List<Menu> menuList = mongoTemplate.find(query, Menu.class);
        List<TreeNode<String>> nodeList = CollUtil.newArrayList();

        String uri = request.getRequestURI();
        for (Menu menu: menuList)
        {
            HashMap map = new HashMap();
            map.put("url",     menu.getUrl());
            map.put("icon",    menu.getIcon());
            map.put("checked", uri.equals("/" + menu.getUrl().toLowerCase()) ? true : false);
            nodeList.add(new TreeNode<>(menu.getMenuId(), menu.getParentId(), menu.getMenuName(), menu.getSort()).setExtra(map));
        }
        List<Tree<String>> treeList = TreeUtil.build(nodeList, "0");
        return treeList;
    }

    @Override
    public Boolean existMenuUrl(String url)
    {
        Query query = Query.query(Criteria.where("url").is(url));
        return mongoTemplate.exists(query, Menu.class);
    }

    @Override
    public Boolean existMenuPermission(String permission)
    {
        Query query = Query.query(Criteria.where("permission").is(permission));
        return mongoTemplate.exists(query, Menu.class);
    }

    @Override
    public Boolean existMenuChildren(String menuId)
    {
        Query query = Query.query(Criteria.where("parent_id").is(menuId));
        return mongoTemplate.exists(query, Menu.class);
    }

    @Override
    public Set<String> getMenuButtonList(String menuUrl)
    {
        Set<String> permissions = new HashSet();
        Query query = Query.query(Criteria.where("url").is(menuUrl));
        Menu menu = mongoTemplate.findOne(query, Menu.class);
        if(ObjectUtil.isEmpty(menu))
        {
           return permissions;
        }

        Query query2 = Query.query(Criteria.where("parent_id").is(menu.getMenuId()));
        List<Menu> menuList = mongoTemplate.find(query2, Menu.class);

        if(menuList.size()>0)
        {
             Map<String, Set<String>> permissionList = menuList.stream().collect(Collectors.groupingBy(Menu::getParentId, Collectors.mapping(Menu::getPermission, Collectors.toSet())));
             permissions = permissionList.get(menu.getMenuId());
        }
        return permissions;
    }
}
