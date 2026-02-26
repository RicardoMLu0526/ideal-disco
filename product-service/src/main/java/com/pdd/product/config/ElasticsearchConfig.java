package com.pdd.product.config;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(RestClientBuilder restClientBuilder) {
        return new RestHighLevelClient(restClientBuilder);
    }

    @Bean
    public RestClientBuilder restClientBuilder(
            @Value("${spring.elasticsearch.uris}") String uris,
            @Value("${spring.elasticsearch.username}") String username,
            @Value("${spring.elasticsearch.password}") String password) {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, password)
        );

        return RestClient.builder(HttpHost.create(uris))
                .setHttpClientConfigCallback(httpClientBuilder -> 
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                );
    }
}
