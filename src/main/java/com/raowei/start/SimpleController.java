package com.raowei.start;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
@Controller
@EnableAutoConfiguration
@ComponentScan(value = "com.raowei.start")
public class SimpleController implements EmbeddedServletContainerCustomizer {
    @Autowired
    private  MakeLoanService makeLoanService;

    private static boolean isSuccess = false;

    static Map<String,String> map = new HashMap<String,String>();
    /**
     *
     */
    @RequestMapping("/")
    @ResponseBody
    public String hello () {
        return "放款";
    }

    @RequestMapping("/makeloan")
    @ResponseBody
    public String makeLoan() {
        makeLoanService.doMakLoan("sdlkfjsd");
        return "放款成功";
    }

    @RequestMapping("/aipg/ProcessServlet")
    @ResponseBody
    public String callBack(HttpServletRequest request, HttpServletResponse response){
        String result;
        String amount = null;
        try {
            ServletInputStream inputStream = request.getInputStream();
            byte buf[] = new byte[4096];
            int count;
            StringBuffer  sb = new StringBuffer();
            while ((count = inputStream.read(buf, 0, buf.length)) > 0)
                sb.append(new String(buf,0,count));
            String requestStr = sb.toString();
            String code = "";


            code = this.match("<QUERY_SN>\\d{10,}</QUERY_SN>","\\d{10,}",requestStr);
            if (code == null || code.equals("")) {
                code = this.match("<REQ_SN>\\d{10,}</REQ_SN>","\\d{10,}",requestStr);
            }
            amount = this.match("<AMOUNT>\\d{2,}</AMOUNT>","\\d{2,}",requestStr);
            if(amount != null && !amount.equals("")) {
                map.put(code,amount);
            }
            result = this.getResult(code);

            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(result.getBytes("GBK"));
            outputStream.flush();
            inputStream.close();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "" ;

    }


    private String match (String match,String match2, String str) {
        Pattern compile = Pattern.compile(match);
        Matcher matcher = compile.matcher(str);
        String result = "";
        while (matcher.find()) {
            String codeFregement = matcher.group();
            Pattern compile1 = Pattern.compile(match2);
            Matcher matcher1 = compile1.matcher(codeFregement);
            while (matcher1.find()) {
                result = matcher1.group();
            }
        }
        return result;
    }


    @RequestMapping("/change/{success}")
    @ResponseBody
    public String change(HttpServletRequest request, @PathVariable String success) {
        if (success.equals("0")) {
            isSuccess = false;
            return "失败状态";
        }else {
            isSuccess = true;
            return "成功状态";
        }

    }


    private String getResult(String code) {

        if (isSuccess) {
            String result = "<?xml version=\"1.0\" encoding=\"GBK\"?><AIPG>#012  <INFO>#012    <TRX_CODE>200004</TRX_CODE>#012    <VERSION>03</VERSION>#012    <DATA_TYPE>2</DATA_TYPE>#012    <REQ_SN>1492398657495</REQ_SN>#012    <RET_CODE>0000</RET_CODE>#012    <ERR_MSG>处理完成</ERR_MSG>#012    <SIGNED_MSG>750340a78a84a127d68a2afb50bb4317d2b48e052f3c40ec09b8c61ac17fbcf418d6fef7a3bf5ee615fa9685a4f4e5e191ea38f916a06b3c08235b1d077e7f49ed2ec925c0ba2f47649f784901d71afdb607f4c1ec55d542264ab09d597490f329b63aaad3426457f5d7b36e0c5d115bb7acf4cb0521cfb9778e6c896c0d5fd0bb6e875909453ec5d7156ecadb3fd8f7e036fb426013f5c29b2fb4a24e3ef2b2c6267c0a917f5a725821ae0d72651c03125d19da31c8f3b43c5c350c68dbd169d35564271ec5c5b9143d80eb84842bd4ff89c910d63da33343a8a037881dcc134299674e7da8a7b559d6a906643b7e6dd2b07e51f3306ffe21864f3b0f1a890c</SIGNED_MSG>#012  </INFO>#012  <QTRANSRSP>#012    <QTDETAIL>#012      <BATCHID>__</BATCHID>#012      <SN>0</SN>#012      <TRXDIR>0</TRXDIR>#012      <SETTDAY>20170417</SETTDAY>#012      <FINTIME>20170417110841</FINTIME>#012      <SUBMITTIME>20170417110336</SUBMITTIME>#012      <ACCOUNT_NO>755926282610201</ACCOUNT_NO>#012      <ACCOUNT_NAME>深圳卫盈智信科技有限公司</ACCOUNT_NAME>#012      <AMOUNT>??</AMOUNT>#012      <REMARK>1704140181</REMARK>#012      <SUMMARY>1704140181</SUMMARY>#012      <RET_CODE>0000</RET_CODE>#012      <ERR_MSG>处理成功</ERR_MSG>#012    </QTDETAIL>#012  </QTRANSRSP>#012</AIPG>";
            result =result.replaceAll("__",code);
            if (map.get(code) != null) {
                return result.replaceAll("\\?\\?",map.get(code));
            }else {
                return result;
            }


        }else {
            String result = "<?xml version=\"1.0\" encoding=\"GBK\"?><AIPG>#012  <INFO>#012    <TRX_CODE>200004</TRX_CODE>#012    <VERSION>03</VERSION>#012    <DATA_TYPE>2</DATA_TYPE>#012    <REQ_SN>__</REQ_SN>#012    <RET_CODE>2007</RET_CODE>#012    <ERR_MSG>提交银行处理</ERR_MSG>#012    <SIGNED_MSG>154734081c05a43cba4e98be7dececbca40b17456e85d97cf78ca83efaa188c7dbf5851d128a91095ac03530d34bb5a2e5bc57c7cd5e9b44b406973e4976c791d1861c8bc762b252e74df202f0c3e07ecb6a5a867ff9ac9952890c3432db773d6b58e987d7d7de69745c18fda0ff50550f1fc90470d773c8c2a7f8805d8fe0d30d7a534b6998f3bd2428967b4674bdca5ede3bbcd0a8823a010b0626f0c84ac84dad54dbdaa6dfcdf576d4d82aa9af5a5772d1b57ac72f832a987bb6b6d332a2d4e06b27b0a635dcbee7c8e3852e83912044c9933c419f8b960f43273168c290294f24b113a5da85cac405cf71f37ff07c4d3139f8c4f4a0708dcabfb9d8fe41</SIGNED_MSG>#012  </INFO>#012  <QTRANSRSP/>#012</AIPG>";
            return result.replaceAll("__",code);
        }
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {

        configurableEmbeddedServletContainer.setPort(8003);
    }

    public static void main(String[] args) {
        SpringApplication.run(SimpleController.class,args);
    }
}
