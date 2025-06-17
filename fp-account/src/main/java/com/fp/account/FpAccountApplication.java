package com.fp.account;

import com.fp.common.config.ExternalServiceAutoConfiguration;
import com.fp.common.properties.ExternalServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class FpAccountApplication {

	public static void main(String[] args) {
		try {
			ConfigurableApplicationContext context = SpringApplication.run(FpAccountApplication.class, args);

			System.out.println("=== 🔍 调试信息 ===");

			// 1. 检查自动配置类是否加载
			try {
				ExternalServiceAutoConfiguration config = context.getBean(ExternalServiceAutoConfiguration.class);
				System.out.println("✅ ExternalServiceAutoConfiguration已加载");
			} catch (Exception e) {
				System.out.println("❌ ExternalServiceAutoConfiguration未加载: " + e.getMessage());
			}

			// 2. 检查配置属性类是否加载
			try {
				ExternalServiceProperties properties = context.getBean(ExternalServiceProperties.class);
				System.out.println("✅ ExternalServiceProperties已加载");
				System.out.println("   - Follow Service URL: " + properties.getFollowService().getUrl());
				System.out.println("   - Follow Service Enabled: " + properties.getFollowService().isEnabled());
				System.out.println("   - Content Service URL: " + properties.getContentService().getUrl());
			} catch (Exception e) {
				System.out.println("❌ ExternalServiceProperties未加载: " + e.getMessage());
			}

			// 3. 检查WebClient beans
			String[] webClientBeans = context.getBeanNamesForType(WebClient.class);
			System.out.println("📦 WebClient类型的Bean数量: " + webClientBeans.length);
			System.out.println("📦 WebClient Bean名称: " + Arrays.toString(webClientBeans));

			// 4. 检查特定的beans
			System.out.println("🔎 特定Bean检查:");
			System.out.println("   - followWebClient存在: " + context.containsBean("followWebClient"));
			System.out.println("   - contentWebClient存在: " + context.containsBean("contentWebClient"));
			System.out.println("   - accountWebClient存在: " + context.containsBean("accountWebClient"));

			// 5. 打印所有beans（用于完整调试）
			System.out.println("📋 总Bean数量: " + context.getBeanDefinitionCount());

		} catch (Exception e) {
			System.out.println("💥 应用启动失败: " + e.getMessage());
			e.printStackTrace();
		}
    }

}
