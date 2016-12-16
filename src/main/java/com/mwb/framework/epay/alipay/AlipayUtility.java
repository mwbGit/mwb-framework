package com.mwb.framework.epay.alipay;

import com.mwb.framework.util.MD5Utility;

import java.util.*;

public class AlipayUtility {
	
	public static Map<String, String> buildSign(Map<String, String> params,
			String key, String charset) {
        //除去数组中的空值和签名参数
        Map<String, String> newParams = AlipayUtility.paraFilter(params);
        //生成签名结果
        String mysign = buildMd5Sign(newParams, key, charset);

        //签名结果与签名方式加入请求提交参数组中
        newParams.put("sign", mysign);
        newParams.put("sign_type", AlipayConfig.SIGN_TYPE);

        return newParams;
    }
	
	/**
	 * 验证签名
	 * @param params
	 * @param sign
	 * @param key
	 * @param charset
	 * @return
	 */
	public static boolean verifySign(Map<String, String> params, String sign,
			String key, String charset) {
		Map<String, String> sParaNew = paraFilter(params);// 过滤空值、sign与sign_type参数
		String mySign = buildMd5Sign(sParaNew, key, charset);// 获得签名结果

		if (mySign.equals(sign)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 验证RSA签名
	 * @param params
	 * @param sign
	 * @param key
	 * @param charset
	 * @return
	 */
	public static boolean verifyRSASign(Map<String, String> params, String sign,
			String key, String charset) {
		Map<String, String> sParaNew = paraFilter(params);// 过滤空值、sign与sign_type参数
		
		String prestr = createLinkString(sParaNew); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		
		return RSA.verify(prestr, sign, key, charset);
	}
	
	/**
	 * RSA签名
	 * @param content
	 * @param privateKey
	 * @param input_charset
	 * @return
	 */
	public static String buildRSASign(String content, String privateKey,
			String input_charset) {
		
		return RSA.sign(content, privateKey, input_charset);
	}
	

	/** 
	 * 除去数组中的空值和签名参数
	 * @param params 签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, String> paraFilter(Map<String, String> params) {

		Map<String, String> result = new HashMap<String, String>();

		if (params == null || params.size() <= 0) {
			return result;
		}

		for (String key : params.keySet()) {
			String value = params.get(key);
			if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
					|| key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	/** 
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * @param params 需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}
	
	/**
	 * 构造签名
	 * @param params
	 * @param key
	 * @param charset
	 * @return
	 */
	private static String buildMd5Sign(Map<String, String> params, String key, String charset) {
		String prestr = createLinkString(params); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		prestr = prestr + key; //把拼接后的字符串再与安全校验码直接连接起来
		return MD5Utility.sign(prestr, charset);
	}
}
