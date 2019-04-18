$(function(){
	$(".error").each(function(){
		showError($(this));
	});
	
	/*1.切换登录按钮的图片*/
	$("#submit").hover(
		function(){
			$("#submit").attr("src","/shop/images/login2.jpg");
			
		},
		function(){
			$("#submit").attr("src","/shop/images/login1.jpg");
		}
	);
	/*2.输入框的到焦点时隐藏错误信息*/
	$(".inputclass").focus(function(){
		var labelid= $(this).attr("id")+"error";
		$("#"+labelid).text("");//把label内容清空
		showError($("#"+labelid));//隐藏没有信息的label
	});
	
	/*3.输入框失去焦点时校验*/
	$(".inputclass").blur(function(){
		var id= $(this).attr("id");
		var funName = "validate"+id.substring(0,1).toUpperCase()+id.substring(1)+"()";/*得到对应的校验函数名*/
		eval(funName);
	});
	/*4.表单提交时校验*/
	$("#submit").submit(function(){
		$("#msg").text("");
		var bool = true;
		if(!validateLoginname()){
			bool = false;
		}
		if(!validateLoginpass()){
			bool = false;
		}
		if(!validateVerifyCode()){
			bool = false;
		}
		return bool;
	});
});
/*登录名校验方法*/
function validateLoginname(){
	/*非空校验*/
	var id ="loginname";
	var value = $("#"+id).val();
	if(!value){
		$("#"+id+"error").text("用户名不能为空！");
		showError($("#"+id+"error"));
		return false;
	}
	/*长度校验*/
	if(value.length<3||value.length>20){
		$("#"+id+"error").text("用户名的长度必须在3~20之间！");
		showError($("#"+id+"error"));
		return false;
	}
	return true;
}

/*登录密码校验方法*/
function validateLoginpass(){
	/*非空校验*/
	var id ="loginpass";
	var value = $("#"+id).val();
	if(!value){
		$("#"+id+"error").text("密码不能为空！");
		showError($("#"+id+"error"));
		return false;
	}
	/*长度校验*/
	if(value.length<3||value.length>20){
		$("#"+id+"error").text("密码的长度必须在3~20之间！");
		showError($("#"+id+"error"));
		return false;
	}
	return true;
}

/*验证码校验方法*/
function validateVerifyCode(){
	/*非空校验*/
	var id ="verifyCode";
	var value = $("#"+id).val();
	if(!value){
		$("#"+id+"error").text("验证码不能为空！");
		showError($("#"+id+"error"));
		return false;
	}
	if(value.length!=4){
		$("#"+id+"error").text("错误的验证码！");
		showError($("#"+id+"error"));
		return false;
	}
	/*验证码校验是否正确*/
	$.ajax({
		url:"/shop/UserServlet",/*要请求的Servlet*/
		data:{method:"ajaxValidateVerifyCode",verifyCode: value},/*给服务器的参数*/
		type:"POST",
		dataType:"json",
		async:false,/*是否异步*/
		cache:false,
		success:function(result){
			if(!result){
				$("#"+id+"error").text("验证码输入错误！");
				showError($("#"+id+"error"));
				return false;
			}
		}
	});
	return true;
}

/*判断当前元素是否有内容，有就显示，没有就隐藏*/
function showError(ele){
	var text = ele.text();
	if(!text){
		ele.css("display","none");
	}else{
		ele.css("display","");
	}
}

/*实现验证码换一张*/
function _change(){
	$("#vCode").attr("src","/shop/VerifyCodeServlet?a="+new Date().getTime());
}