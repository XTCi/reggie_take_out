package com.example.reggie_take_out.common;

public class BaseContext {
    /**
     * 根据线程获取id
     */
    private static ThreadLocal<Long>  threadLocal = new ThreadLocal<>();
    public static void setCurrendId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrendId(){
        return threadLocal.get();
    }
}
