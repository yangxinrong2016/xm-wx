package com.imxiaomai.wxplatform.weixin.service;

import com.imxiaomai.wxplatform.common.Constants;
import com.imxiaomai.wxplatform.domain.Keyword;
import com.imxiaomai.wxplatform.domain.ResponseText;
import com.imxiaomai.wxplatform.service.impl.KeywordServiceImpl;
import com.imxiaomai.wxplatform.service.impl.ResponseTextServiceImpl;
import com.imxiaomai.wxplatform.util.ResourceUtil;
import com.imxiaomai.wxplatform.util.Xml;
import com.imxiaomai.wxplatform.weixin.exception.WXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

@Service("textMsgService")
public class TextMsgService {
    
    private static final Logger log = LoggerFactory.getLogger(TextMsgService.class);
    @Resource
    KeywordServiceImpl keywordServiceImpl;
    @Resource
    ResponseTextServiceImpl responseTextServiceImpl;
    /**
     * 得到文本内容的回复
     * @param wxId
     * @return
     * @throws WXException 
     */
    public String getResponseContent(String wxId, String openid, long createTime, Xml.ParsedXml parsedXml) throws WXException {
        
        String retString = "";
        
        String content = parsedXml.text("Content");

        //add by tom, 20150227
        //根据不同的关键字做一些触发操作，比如输入"客服"，就接入多客服系统
        //先恶心实现吧，这里比较适合用filter设计模式
        if(content.equals("客服") || content.equals("0") || content.equals("[0]")){
            retString = "<xml>\n" +
                "<ToUserName><![CDATA["+openid+"]]></ToUserName>\n" +
                "<FromUserName><![CDATA["+wxId+"]]></FromUserName>\n" +
                "<CreateTime>"+createTime+"</CreateTime>\n" +
                "<MsgType><![CDATA[transfer_customer_service]]></MsgType>\n" +
                "</xml>";
            return retString;
        }
        //end add
        retString = getResponseContent(wxId, openid, createTime, content);
        return retString;
        
    }
    
    public String getResponseContent(String wxId, String openid, long createTime, String content) throws WXException {
        String retString = "";
        try {
            Keyword keyword = keywordServiceImpl.getByContent(wxId, content);
            if(null == keyword){
                log.error("默认关键字都木有，r u kidding me V_V, wxId:{}, openid:{}, content:{}", wxId, openid, content);
                throw new WXException(String.format("默认关键字都木有，r u kidding me V_V, wxId:%s, openid:%s, content:%s", wxId, openid, content));
            }
            if(keyword.getResponsetype().equals(Constants.WX_RESPONSE_TYPE_TEXT)) {
                ResponseText responseText = responseTextServiceImpl.selectByPrimaryKey(keyword.getResponseid());
                if(null == responseText) {
                    log.error("没有回复的文本消息, id:{}", keyword.getResponseid());
                    throw new WXException("没有回复的文本消息, id:{}" + keyword.getResponseid());
                }
                retString = formatFromTemplate(wxId, openid, responseText.getContent());
            }else{
                log.error("不支持的回复消息类型:{}, 目前只支持text类型", keyword.getResponsetype());
                throw new WXException("不支持的回复消息类型:"+keyword.getResponsetype()+", 目前只支持text类型");
            }
            
        } catch (UnsupportedEncodingException e) {
            log.error("不支持的编码, "+e.getMessage());
            e.printStackTrace();
            throw new WXException("不支持的编码, "+e.getMessage());
        }
        
        return retString;
        
    }
    
    public String getDefaultResponseContent(String wxId, String openid, long createTime) throws WXException {
        return getResponseContent(wxId, openid, createTime, Constants.DEFAULT_RESPONSE_CONTENT);
    }

    private String formatFromTemplate(String wxId, String openid, String content) throws UnsupportedEncodingException {
        
        byte[] response = ResourceUtil.classpathAsBytes("tpl_text_response.txt");
        
        String responseString = new String(response,"UTF-8");
        
        long nowSec = System.currentTimeMillis()/1000;
        String retString = String.format(responseString, openid, wxId, nowSec, content);
        
        log.debug("回复的文本消息:\n{}", retString);
        
        return retString;
    }
}
