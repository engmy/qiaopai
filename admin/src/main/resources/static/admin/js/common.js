;!function(){

	let layer = layui.layer, $ = layui.jquery, form = layui.form, element = layui.element, util= layui.util, table= layui.table, laydate = layui.laydate;

	let lastTime = new Date().getTime(); // 最后操作的时间
	let currentTime = new Date().getTime(); // 最新的时间
	let timeOut = 1*1000*60*20; // 单位 毫秒，设置超时时间：20分钟

	$(document).mouseover(function()
	{
		lastTime = new Date().getTime();
	});

	/**
	 * 登录超时检测
	 */
	verifyLoginTimeout = function()
	{
		currentTime = new Date().getTime();
		if(currentTime - lastTime > timeOut)
		{
			// 判断当前页是否是在登录页 若在登录页则不需要退出操作
			if(window.location.pathname == "/admin/login"){
				return;
			}
			$.get('/admin/logout',function(json)
			{
				if(json.code == 200)
				{
					layer.msg("登录超时，即将退出", {time: 3000}, function ()
					{
						window.location.href = json.data;
					});
				}else{
					layer.msg(json.msg);
				}
			},'json');
		}
	}
	window.setInterval(verifyLoginTimeout, 1000 * 5);

	/**
	 * 密码强验证
	 * 密码必须12位以上，并且有至少有一个大写字母 &一个数字 & 一个特殊字符
	 * @param value
	 * @returns {string}
	 */
	strongVerifiOfPassword = function (value)
	{
		if(value.length < 12 || value.length > 24){
			return "密码长度为12-24位";
		}
        if(value.indexOf(" ") !== -1){
            return "存在空格字符";
        }
		if(!new RegExp("^(?=.*?[A-Za-z]+)(?=.*?[0-9]+)(?=.*?[A-Z])(?=.*?[!@#$%^&*]).*$").test(value)){
			return "至少要有一个大写字母、一个数字、一个特殊字符";
		}
	}

	/**
	 * 验证输入的字符串中是否存在空格
	 * @param value
	 */
	verifySpace = function (value)
	{
		if(value.indexOf(" ") !== -1){
			return "存在空格字符";
		}
	}

	/**
	 * 验证输入框的值是否存在特殊字符
	 * @param value
	 */
	verifInputValue = function (value)
	{
		let regEn = /[`~!@#$%^&*()_+<>?:"{}=,\/;'[\]]/im,
			regCn = /[·！#￥（——）：；“”‘、，|《。》？、【】[\]]/im;
		if(regEn.test(value) || regCn.test(value)){
			return "含有特殊字符";
		}
	}

	/**
	 * 禁止输入中文
	 * @param value
	 */
	prohibitChinese = function (value)
	{
		var reg = new RegExp("[\\u4E00-\\u9FFF]+","g");
		if(reg.test(value)){
			return "不能含有中文字符";
		}
	}

	/**
	 * 验证输入框的长度
	 * @param value
	 * @param len
	 * @returns {string}
	 */
	verifLength = function (value,len)
	{
		if(value.length > len){
			return "长度不得超过 " + len + " 位";
		}
	}

	/**
	 * 统一封装表格渲染方式
	 * @param url
	 * @param cols
	 * @returns {{elem: string, toolbar: string, defaultToolbar: *[], response: {statusName: string, statusCode: number}, limit: number, parseData: (function(*): {msg: *, code: *, data: *, count: *}), page: boolean, cols: *[], done: obj.done, url, limits: number[]}}
	 */
	renderParam = function (url, cols, toolbar)
	{
		url = url.replace(/(\+)/g, "");
		let obj = {
			elem: '#table',
			url: url,
			limit: 15,
			limits: [15,30,45,60,75,90,105,120,135,150],
			page: true,
			toolbar: toolbar==false ? false : "#toolbar",
			defaultToolbar: [],
			cols: [cols],
			response:{
				statusName: 'code',
				statusCode: 200
			},
			parseData: function(res)
			{
				return {
					"code":  res.code,
					"msg":   res.msg,
					"count": res.data.total,
					"data":  res.data.list
				};
			}
		}
		return obj;
	}

	/**
     * 获取url简化
	 */
	var getUrl = function(url)
	{
		return location.protocol+'//'+window.location.host + '/' + url;
	}

	/**
     * 展开与收拢
	 */
	optMore = function(obj)
	{
		$(obj).parents('tr').next('tr').toggle();
	}

	/**
     * 气泡提示
	 */
	tips = function(id,text)
	{
		if(id){
			layer.tips(text,"#"+id, {
				tips  : [1, '#009688'],
				time  : 2000,
				area  : ['230px', 'auto']
			});
		}
	}

	/**
     * 全选和全不选
	 */
	form.on('checkbox(checkAll)', function(data)
	{
		var flag = data.elem.checked;
		$('.layui-form input:checkbox').prop("checked", flag)
		form.render('checkbox');
	});

	/**
     * 获取选中的checked的ID
	 */
	getcheckeds = function(obj, filed)
	{
		var ids = [];
		var checkStatus = table.checkStatus(obj.config.id);
		$.each(checkStatus.data, function (index, row)
		{
			ids.push(row[filed]);
		});
		return ids.join(',');
	}

	/**
     * 批量删除方法
	 * @param url  删除的地址url
	 * @param id   单独删除的ID,当ID为空时为批量删除
	 * @param text 删除时的文字提示
	 * @param jump 跳转地址
	 */
	deleteData = function(url, pids, text, jump)
	{
		var jump = jump ? jump : '';
		var text = text ? text:'您确定要删除吗？';

		if(pids && url)
		{
			layer.confirm(text,
			function(){
				$.post(url,{id:pids},function(json){
					if(json.status==1)
					{
						layer.closeAll();
						jump?reload(jump):reload();
					}else{
						layer.alert(json.msg);
					}
				},'json');
			})
		}else{
			layer.msg('请选择要删除的数据！');
		}
	}

	/**
     * 更新状态
	 * @param url  		  更新的地址url
	 * @param id(int) 	  当ID为空时为更新数据;
	 * @param status(int) 需要更新的状态;
	 */
	updateData = function(url, id, status)
	{
		var ids = id ? id : getcheckeds();
		if(ids && url)
		{
			$.post(url,{'id':ids, 'status':status},
			function(json)
			{
				if(json.status==1)
				{
					layer.alert(json.msg, {title:'系统提示'}, function()
					{
						reload();
					});
				}else{
					layer.alert(json.msg);
				}
			},'json');
		}else{
			layer.msg('至少选择一条数据!',2,0);
		}
	}

	/**
	 * layer弹框简化
	 */
	showBox = function(title, width, height, close, url, success)
	{
		layer.open({
			type: 2,
			title: title,
			shadeClose: false,
			closeBtn: close,
			shade: 0.5,
			area: [width+'px', height+'px'], //宽高
			content: url,
			success: success
		});
	}

  /**
	* 省级联动
	* @param url(string)  请求路径 必填
	* @param province(int)省级  回显时填写
	* @param city(int) 	  市级  回显时填写
	* @param county(int)  区级  回显时填写
	*/
	getArea = function(url,province,city,county)
	{
		var province = arguments[1]?arguments[1]:'',city = arguments[2]?arguments[2]:'',county = arguments[3]?arguments[3]:'';

		$.post(url,{pid:1},function(json)
		{
			if(json)
			{
				var option = [];

				$(json).each(function(index,rows){

					if(province==rows['id'])
					{
						option +='<option value="'+rows['id']+'" selected>'+rows['name']+'</option>';

						if(city)
						{
							getAreaList('select[name=city]',url,province,city,'请选择城市');
						}

						if(county)
						{
							getAreaList('select[name=county]',url,city,county,'请选择县/区');
						}
					}else{
						option +='<option value="'+rows['id']+'">'+rows['name']+'</option>';
					}
				})

				$('select[name=province]').html('<option value="">请选择省份</option>'+option);
				form.render('select');
			}
		},'json');


		form.on('select(province)', function(data)
		{
			var pid = data.value;
			getAreaList('select[name=city]',url,pid,'','请选择城市');
            $('select[name=county]').html('<option value="">请选择县/区</option>');
		})

		form.on('select(city)', function(data)
		{
			var pid = data.value;
			getAreaList('select[name=county]',url,pid,'','请选择县/区');
		})

		getAreaList = function(dom,url,pid,choose,text)
		{
			$.post(url,{pid:pid},
			function(json){
				if(json)
				{
					var option = [];

					$(json).each(function(index,rows)
					{
						if(choose==rows['id'])
						{
							option +='<option value="'+rows['id']+'" selected>'+rows['name']+'</option>';
						}else{
							option +='<option value="'+rows['id']+'">'+rows['name']+'</option>';
						}
					})

					$(dom).html('<option value="">'+text+'</option>'+option);
					form.render('select');
				}
			},'json');
		}
	}

	/**
	 * 格式化金钱
	 * @param url(string)  请求路径 必填
	 * @param province(int)省级  回显时填写
	 * @param city(int) 	  市级  回显时填写
	 * @param county(int)  区级  回显时填写
	 */
	formatPrice = function(str)
	{
		var newStr = "", count = 0;
		str = str.toString();

		if(str.indexOf(".") == -1){
			for(var i=str.length-1; i >= 0; i--){
				if(count % 3 == 0 && count != 0){
					newStr = str.charAt(i) + "," + newStr;
				}else{
					newStr = str.charAt(i) + newStr;
				}
				count++;
			}

			str = newStr + ".00"; //自动补小数点后两位
			return str;
		}else{
			for(var i = str.indexOf(".")-1; i >= 0; i--){
				if(count % 3 == 0 && count != 0){
					newStr = str.charAt(i) + "," + newStr;
				}else{
					newStr = str.charAt(i) + newStr; //逐个字符相接起来
				}
				count++;
			}

			str = newStr + (str + "00").substr((str + "00").indexOf("."),3);
			return str;
		 }
	}

	/**
	 * 字数输入限制
	 * @param field (string) 表单名字
	 * @param countfield 表单域元素名
	 * @param maxlimit   限制字符
	 */
	textCounter = function(field, countfield, maxlimit)
	{
	   if ($("#"+field).val().length > maxlimit)
	   {
		   $("#"+field).val($("#"+field).val().substring(0, maxlimit));
	   }
	   else
	   {
		   $("#"+countfield).text(maxlimit - $("#"+field).val().length);
	   }
    }

	/**
	 * layer load简单封装
	 * @param text load文字提示
	 */
	load = function(text)
	{
		layer.load(2, {content:text,success:
			function(msg)
			{
				msg.find('.layui-layer-content').css({'padding-top':'5px','padding-left':'40px','width':'150px'});
			}
		})
	}

	/**
	 * 清理缓存
	 * @param submitId  按钮lay-filter
	 * @param url 		提交地址
	 * @param formId    表单ID
	 * @param callback  回调方法
	 */
	clearup = function()
	{
		load('正在清理缓存...');
		$.post(getUrl('admin/index/clear'),function()
		{
			layer.closeAll();
			layer.msg('清理缓存完毕！');
		},'json');
	}

	/**
	 * Ajax异步提交表单
	 * @param submitId  按钮lay-filter
	 * @param url 		提交地址
	 * @param formId    表单ID
	 * @param callback  回调方法
	 */
	ajaxform = function (submitId, url, formId, callback)
	{
		form.on('submit('+submitId+')', function(data)
		{
			$.post(url,$('#'+formId+'').serialize(),
			function(data)
			{
				return callback(data);
			},'json');

			return false;
		});
	}

	/**
     * Pjax加载页面
	 */
	$(".pjax").click(function()
	{
		$(this).parents('ul').find('li').removeClass('layui-nav-itemed');
		$(this).parents('li').addClass('layui-nav-itemed');

		$(document).ready(
			function(){
				layer.closeAll()
			}
		).on('pjax:send',
			function()
			{
				load('正在努力加载页面....');
			}
		).on('pjax:complete',
			function(){
				form.render();element.init();layer.closeAll();
			}
		)
		$(document).pjax('.pjax','#page-wrapper',{
			timeout:9000
		});
	})

	/**
     * Pjax 表单提交表单
	 */
	$("#page-wrapper").on('submit', '.pjax-from',function (event)
	{
		var method = $(this).attr('method') ? $(this).attr('method') : 'GET';
		event.preventDefault();

		$(document).ready
		(
			function(){layer.closeAll()}
		).on('pjax:send',
			function(){load('正在努力加载页面....')}
		).on('pjax:complete',
			function(){form.render();element.init();layer.closeAll();}
		)

		$.pjax.submit(event, '#page-wrapper', {type:method, timeout:9000});
	});

	/**
     * Pjax刷新页面
	 */
	reload = function(url)
	{
		if(url)
		{
			window.location.href = url
		}else{
			window.location.reload();
		}

		/*
		$(document).ready
		(
			function(){layer.closeAll()}
		).on('pjax:send',
			function(){load('正在努力加载页面....')}
		).on('pjax:complete',
			function(){form.render();element.init();layer.closeAll();}
		)

		if(url)
		{
			$.pjax({url:url,container: '#page-wrapper',timeout:5000});
		}else{
			$.pjax.reload('#page-wrapper');
		}*/
	}

	/**
     * 退出登录系统
	 */
	$('#logout-btn').click(function()
	{
		layer.confirm('您确定要退出本系统吗？',{title:'系统提示'}, function()
		{
			$.get('/admin/logout',function(json)
			{
				if(json.code==200)
				{
					window.location.href = json.data;
				}else{
					layer.msg(json.msg);
				}
			},'json');
		});
	});

	/**
	 * 查看圖片
	 * @param submitId  按钮lay-filter
	 */
	showImages = function(dom)
	{
		layer.photos({
			photos: dom,
			anim:5,
			shade: false,
			area:'400px',
			closeBtn:1
		})
	}

	Array.prototype.remove = function(val)
	{
		var index = this.indexOf(val);
		if (index > -1) {
			this.splice(index, 1);
		}
	};

	$(".layui-nav-tree .layui-nav-item .layui-nav-child dd").each(function (index, obj)
	{
		if($(obj).hasClass("layui-this"))
		{
			$(obj).parents("li").addClass("layui-nav-itemed");
		}
	})

	layDateRang = function (startTime, endTime)
	{
		var startDate = laydate.render({
			elem: startTime,
			btns: ['clear', "confirm"], //只显示清空和确定按钮
			ready: function(date)
			{
               if($(endTime).val())
               {
				   var mindate = $(endTime).val().split("-");
				   startDate.config.min = {//结束日期的最小值
					   year: date.year,
					   month: date.month - 1,
					   date: 1,
				   };
				   startDate.config.max = {//结束日期的最大值
					   year: date.year,
					   month: date.month - 1,
					   date: mindate[2] * 1,//最大为这个月的最后一天
				   };
			   }
			},
			done: function (value, date)
			{
				var monthDay = laydate.getEndDate(date.month, date.year);//获取这个月一共有多少天
				if (value !== '') {
					endDate.config.min = {//结束日期的最小值
						year: date.year,
						month: date.month - 1,
						date: date.date,
					};
					endDate.config.max = {//结束日期的最大值
						year: date.year,
						month: date.month - 1,
						date: monthDay,//最大为这个月的最后一天
					};
				} else {//清空之后，可继续选择，日期范围可自己配置
					endDate.config.min = {
						year: 1900,
						month: 0,
						date: date.date,
					};
					endDate.config.max = {
						year: 2099,
						month: date.month - 1,
						date: date.date,
					};
				}
			}
		});
		var endDate = laydate.render({
			elem: endTime,
			btns: ["clear", "confirm"],
			ready: function(date)
			{
				if($(startTime).val())
				{
					var mindate = $(startTime).val().split("-");
					var monthDay = laydate.getEndDate(date.month, date.year);// 获取这个月一共有多少天
					endDate.config.min = {//结束日期的最小值
						year: date.year,
						month:  date.month - 1,
						date:  mindate[2] * 1,
					};
					endDate.config.max = {//结束日期的最大值
						year: date.year,
						month: date.month - 1,
						date: monthDay,//最大为这个月的最后一天
					};
				}
			},
			done: function (value, date)
			{
				if (value !== '') {
					startDate.config.max = {//起始日期最大值
						year: date.year,
						month: date.month - 1,
						date: date.date,
					}
					startDate.config.min = {//起始日期最小值为1号
						year: date.year,
						month: date.month - 1,
						date: 1,
					}
				} else {
					startDate.config.max = {
						year: 2099,
						month: date.month - 1,
						date: date.date,
					}
					startDate.config.min = {
						year: 1900,
						month: 0,
						date: 1
					}
				}
			}
		});
	}

	//进入全屏
	fullscreen = function()
	{
		var docElm = document.documentElement;
		if (docElm.requestFullscreen) {
			docElm.requestFullscreen();
		}
		//FireFox
		else if (docElm.mozRequestFullScreen) {
			docElm.mozRequestFullScreen();
		}
		//Chrome等
		else if (docElm.webkitRequestFullScreen) {
			docElm.webkitRequestFullScreen();
		}
		//IE11
		else if (elem.msRequestFullscreen) {
			elem.msRequestFullscreen();
		}
	}

	// 退出全屏
	exitscreen = function ()
	{
		if (document.exitscreen)
		{
			document.exitscreen();
		}
		else if (document.mozCancelFullScreen)
		{
			document.mozCancelFullScreen();
		}
		else if (document.webkitCancelFullScreen) {
			document.webkitCancelFullScreen();
		}
		else if (document.msExitFullscreen) {
			document.msExitFullscreen();
		}
	}

	var screen = 0;
	$('#fullscreen').on('click', function ()
	{
		screen ++;
		screen % 2===1 ? fullscreen() : exitscreen();
	});

	form.render();
	element.init();
}();
