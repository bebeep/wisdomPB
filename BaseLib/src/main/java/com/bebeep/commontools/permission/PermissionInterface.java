package com.bebeep.commontools.permission;

/**
 * Created by Bebeep
 * Time 2018/5/11 12:15
 * Email 424468648@qq.com
 * Tips
 */

public interface PermissionInterface {
    /**
     * 可设置请求权限请求码
     */
    int getPermissionsRequestCode();

    /**
     * 设置需要请求的权限
     */
    String[] getPermissions();

    /**
     * 请求权限成功回调
     */
    void requestPermissionsSuccess();

    /**
     * 请求权限失败回调
     */
    void requestPermissionsFail();
}
