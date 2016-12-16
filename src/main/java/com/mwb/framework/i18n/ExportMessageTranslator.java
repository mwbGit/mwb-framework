package com.mwb.framework.i18n;


/**
 * @author anseirai
 * 
 * 这部分message获取与之前的区别:
 * 此处的key: export.<vo simple class name>.code
 * 之前的key: enum.<enum class name>.code
 * 
 */
public class ExportMessageTranslator {
    public static String getName(String voSimpleClassName, String code) {
        return MessageSourceManager.getMessage("export." + voSimpleClassName + "." + code);
    }
}
