package com.yuansfer.sdk.model;

import java.util.HashMap;
/**
* @Author Fly-Android
* @CreateDate 2019/5/27 9:30
* @Desciption 请求参数实体
*/
public abstract class ParamInfo {

    public abstract HashMap<String, String> toHashMap();
}
