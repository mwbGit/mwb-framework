package com.mwb.framework.epay.alipay;

public class AlipayConfig {
	
	// services
	public static final String SERVICE_CREATE_TRANSACTION = "create_partner_trade_by_buyer";
	public static final String SERVICE_NOTIFY_VERIFY = "notify_verify";
	public static final String SERVICE_SEND_GOODS = "send_goods_confirm_by_platform";
	public static final String SERVICE_CLOSE_TRANSACTION = "close_trade";
	
	// payment type
	public static final String PAYMENT_TYPE = "1";
  
    // 商品数量，必填，建议默认为1，不改变值，把一次交易看成是一次下订单而非购买一件商品
	public static final String PRODUCT_QUANTITY = "1"; 
    
    // 物流费用。必填，即运费
	public static final String LOGISTICS_FEE = "0.00"; 

	// 物流支付方式，必填，两个值可选：SELLER_PAY（卖家承担运费）、BUYER_PAY（买家承担运费）
	public static final String LOGISTICS_PAYMENT = "SELLER_PAY";
	
	// 物流类型。 必填，三个值可选：EXPRESS（快递）、POST（平邮）、EMS（EMS）
	public static final String LOGISTICS_TYPE = "EXPRESS";
	
	// 配送公司
	public static final String DELIVER_COMPANY = "生活半径";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String INPUT_CHARSET = "utf-8";
	
	// 签名方式 不需修改
	public static String SIGN_TYPE = "MD5";
	
	// body length in the create trade body
	public static final int TRADE_BODY_LENGTH = 100; //400

}
