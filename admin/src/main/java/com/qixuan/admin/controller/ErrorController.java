package com.qixuan.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorController
{
	@RequestMapping(value = "404.html", method = RequestMethod.GET)
	public String error()
	{
		return "admin/public/404";
	}
}
