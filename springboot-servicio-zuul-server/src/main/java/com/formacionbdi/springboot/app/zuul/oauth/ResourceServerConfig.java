package com.formacionbdi.springboot.app.zuul.oauth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@RefreshScope
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{

	@Value("${config.security.oauth.jwt.key}")
	private String jwtKey;
	
	//Este es para proteger nuestras rutas/endopoints 
	@Override
	public void configure(HttpSecurity http) throws Exception {
		//http.authorizeRequests().antMatchers("/api/security/oauth/token").permitAll();
		http.authorizeRequests().antMatchers("/api/security/oauth/**").permitAll()
		.antMatchers(HttpMethod.GET, "/api/productos/listar", "/api/items/listar", "/api/usuarios/usuarios").permitAll()
		.antMatchers(HttpMethod.GET, "/api/productos/ver/{id}", 
									"/api/items/ver/{id}/cantidad/{cantidad}", 
									"/api/usuarios/usuarios/{id}").hasAnyRole("ADMIN", "USER")
		/**
		.antMatchers(HttpMethod.POST, "/api/productos/crear", 
									  "/api/items/crear" , 
									  "/api/usuarios/usuarios").hasRole("ADMIN")
		.antMatchers(HttpMethod.PUT, "/api/productos/editar/{id}", 
									 "/api/items/editar/{id}",
									 "/api/usuarios/usuarios/{id}").hasRole("ADMIN")
		.antMatchers(HttpMethod.DELETE, "/api/productos/editar/{id}", 
				 						"/api/items/editar/{id}",
				 						"/api/usuarios/usuarios/{id}").hasRole("ADMIN");
		**/
		//RESUMIDO ojo, las genericas tiene que ir la última de la lista para no bloquear GET
		.antMatchers( "/api/productos/**", 
				  "/api/items/**" , 
				  "/api/usuarios/**").hasRole("ADMIN")
		//opecional esto sería el default:
		.anyRequest().authenticated()
		//configuracion CORS:
		.and().cors().configurationSource(corsConfigurationSource())
		;
		
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfig=new CorsConfiguration();
		//Ej1:
		//corsConfig.addAllowedOrigin("http://localhost:4200");
		//Ej2 Lista
		//corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:4200","http://localhost:4800"));
		corsConfig.setAllowedOrigins(Arrays.asList("*"));
		corsConfig.setAllowedMethods(Arrays.asList("POST","GET","PUT","DELETE","OPTIONS"));
		corsConfig.setAllowCredentials(true);
		corsConfig.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));
		UrlBasedCorsConfigurationSource source =new UrlBasedCorsConfigurationSource();
		//para que aplique a todas las rutas
		source.registerCorsConfiguration("/**", corsConfig);
		
		return source;
	}
	
	//Sirve para registrar un filtro de cors que quede configurado a nivel global en el filtro de spring para toda la aplicacion, 
	//no solo en spring security
	@Bean 
	public FilterRegistrationBean<CorsFilter> corsFilter(){
		FilterRegistrationBean<CorsFilter>  bean=new FilterRegistrationBean<CorsFilter>(new CorsFilter(corsConfigurationSource()));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;		
	}

	//Este es para configurar el token storage
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore());
	}

	

	@Bean
	public  JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter tokenConverter=new JwtAccessTokenConverter();
		tokenConverter.setSigningKey(jwtKey);
		return tokenConverter;
	}
	

	

}
