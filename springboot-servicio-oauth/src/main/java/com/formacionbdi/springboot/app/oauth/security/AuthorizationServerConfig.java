package com.formacionbdi.springboot.app.oauth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter{

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	//Permisos que van a tener nuestros endpoints para generar el token y validarlo
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		// cualquiera puede acceder a esta ruta:  /token/auth
		security.tokenKeyAccess("permitAll()")
		//se encarga de validar el token esta ruta requiere autenticacion
		.checkTokenAccess("isAuthenticated()")
		;
	}

	//Aquí se registran los clientes:
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		//usamos inMemory, pero podríamos utilizar JDB oe l que quisieramos
		clients.inMemory()
		//clientId=frontendapp		
		.withClient("frontendapp")
		//secret:12345
		.secret(passwordEncoder.encode("12345"))
		//scopes
		.scopes("read","write")
		//grantypes password, autorization_code, implicit
		.authorizedGrantTypes("password", "refresh_token")
		//1 hora
		.accessTokenValiditySeconds(3600)
		//1 hora
		.refreshTokenValiditySeconds(3600)
		/** 2º cliente
		.and()
		.withClient("androidapp")
		//secret:54321
		.secret(passwordEncoder.encode("54321"))
		//scopes
		.scopes("read","write")
		//grantypes password, autorization_code, implicit
		.authorizedGrantTypes("password", "refresh_token")
		//1 hora
		.accessTokenValiditySeconds(3600)
		//1 hora
		.refreshTokenValiditySeconds(3600)
		**/
		;
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager)
		.tokenStore(tokenStore())
		.accessTokenConverter(accessTokenConverter());
	}

	@Bean
	public  JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter tokenConverter=new JwtAccessTokenConverter();
		tokenConverter.setSigningKey("algun_codigo_secreto_aeiou");
		return tokenConverter;
	}
	
	
	
	
}
