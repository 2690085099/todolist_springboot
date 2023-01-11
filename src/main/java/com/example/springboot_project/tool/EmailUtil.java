package com.example.springboot_project.tool;

import com.example.springboot_project.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;

/**
 * 发送邮件工具类 MailUtil
 */

@Service
public class EmailUtil implements EmailService {
    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    //Spring Boot 提供了一个发送邮件的简单抽象，使用的是下面这个接口，这里直接注入即可使用
    private final JavaMailSender mailSender;
    
    // 配置文件中我的QQ邮箱
    @Value("${spring.mail.from}")
    private String from;
    
    /*
     * 发送邮件对象
     * */
    private final JavaMailSender javaMailSender;
    
    /*
     * Thymeleaf的HTML渲染对象
     * */
    private final TemplateEngine templateEngine;
    
    public EmailUtil(JavaMailSender mailSender, JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }
    
    /**
     * 简单文本邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        // 创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 邮件发送人
        message.setFrom(from);
        // 邮件接收人
        message.setTo(to);
        // 邮件主题
        message.setSubject(subject);
        // 邮件内容
        message.setText(content);
        // 发送邮件
        mailSender.send(message);
    }
    
    /**
     * 发送HTML邮件
     *
     * @param to      收件人,多个时参数形式 ："xxx@xxx.com,xxx@xxx.com,xxx@xxx.com"
     * @param subject 主题
     * @param content 内容
     */
    @Override
    public void sendHtmlMail(String to, String subject, String content) {
        //获取MimeMessage对象
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(message, true);
            // 邮件发送人
            messageHelper.setFrom(from);
            // 邮件接收人,设置多个收件人地址
            InternetAddress[] internetAddressTo = InternetAddress.parse(to);
            messageHelper.setTo(internetAddressTo);
            // 邮件主题
            message.setSubject(subject);
            // 邮件内容，html格式
            messageHelper.setText(content, true);
            // 发送
            mailSender.send(message);
            // 日志信息
            logger.info("邮件已经发送。");
        } catch (Exception e) {
            logger.error("发送邮件时发生异常！", e);
        }
    }
    
    /**
     * 带附件的邮件
     *
     * @param to       收件人
     * @param subject  主题
     * @param content  内容
     * @param filePath 附件
     */
    @Override
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            helper.addAttachment(fileName, file);
            mailSender.send(message);
            //日志信息
            logger.info("邮件已经发送。");
        } catch (Exception e) {
            logger.error("发送邮件时发生异常！", e);
        }
    }
    
    /**
     * Thymeleaf邮件模板发送邮件
     *
     * @param userEmail 用户要注册的邮箱
     * @param verificationCode 邮箱注册验证码
     * @throws MessagingException 消息异常
     */
    public void sendThymeleafMail(String userEmail,String verificationCode) throws MessagingException{
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        // 发送复杂邮件用的消息助手
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        // 主题
        helper.setSubject("星辰速记网站验证码");
        // 发件人的邮箱
        helper.setFrom("2690085099@qq.com");
        // 发送日期
        //helper.setSentDate(new Date());
        // 要发给的邮箱
        helper.setTo(userEmail);
        // 密送的邮箱
        //helper.setBcc("1326303027@qq.com");
        // 抄送邮箱
        //helper.setCc("1326303027@qq.com");
        // 快速回复
        //helper.setReplyTo("1326303027@qq.com");
        // 开始配置FreeMarker邮件模板
        Context context = new Context();
        // 邮件模板参数
        context.setVariable("userEmail", userEmail);
        context.setVariable("verificationCode", verificationCode);
        // thymeleaf.html:映射的html名字——文件在resources/templates目录中
        String process = templateEngine.process("thymeleaf.html", context);
        // 邮件内容(html渲染 所以要填true)
        helper.setText(process, true);
        // 内嵌图片
        /*FileSystemResource aaResource = new FileSystemResource(new File("D:\\aa.png"));
        // 内嵌的名字 'aa'要和 html里img标签中cid:{值}一致
        helper.addInline("aa", aaResource);*/
        
        // 附件(多个 就addAttachment()多个)
        /*FileSystemResource resource = new FileSystemResource(new File("D:\\采购需求单.xls"));
        helper.addAttachment(
                Objects.requireNonNull(resource.getFilename()),//附件名称
                resource,  //附件流
                Files.probeContentType(Paths.get(Objects.requireNonNull(resource.getFilename()))));//附件类型*/
        // 发送邮件
        javaMailSender.send(mimeMessage);
    }
}