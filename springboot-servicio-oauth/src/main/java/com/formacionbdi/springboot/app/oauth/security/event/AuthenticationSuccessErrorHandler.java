package com.formacionbdi.springboot.app.oauth.security.event;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.formacionbdi.springboot.app.oauth.services.IUsuarioService;
import com.formacionbdi.springboot.app.usuarios.commons.models.entity.Usuario;

import brave.Tracer;
import feign.FeignException;

@Component
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher{

	Logger log=LoggerFactory.getLogger(AuthenticationSuccessErrorHandler.class);
	
	@Autowired 
	IUsuarioService usuarioService;
	
	//Lo añadimos para crear nuestros tags de zipkin
	@Autowired
	Tracer tracer;
	
	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		UserDetails user= (UserDetails)authentication.getPrincipal();
		log.info("Success login:"+user.getUsername());
		try {
			Usuario usuario=usuarioService.findyByUsername(user.getUsername());											
			if(usuario.getIntentos()!=null && usuario.getIntentos()>0) {
				usuario.setIntentos(0);
				usuarioService.update(usuario, usuario.getId());	
			}			
		}catch(FeignException fe) {
			log.error(String.format("El usuario %s no existe en el sistema  error:",authentication.getName(),fe));
		}
		
		
	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		
		String mensaje="Error en el login: "+ exception.getMessage();
		log.error(mensaje);
		
		try {
			StringBuilder errors=new StringBuilder();
			errors.append(mensaje);
			Usuario usuario=usuarioService.findyByUsername(authentication.getName());
			if(usuario.getIntentos()==null) {
				usuario.setIntentos(0);
			}
			usuario.setIntentos(usuario.getIntentos()+1);
			log.error(String.format("El usuario %s intento %d",authentication.getName(),usuario.getIntentos()));
			
			errors.append(" - " + String.format("El usuario %s intento %d",authentication.getName(),usuario.getIntentos()));
			
			if(usuario.getIntentos()>2) {
				String errorMaxIntentos=String.format("El usuario %s deshabilitado por máximo intentos",authentication.getName());
				log.error(errorMaxIntentos);
				errors.append(" - " + errorMaxIntentos);
				
				usuario.setEnabled(false);
			}
			usuarioService.update(usuario, usuario.getId());
			
			tracer.currentSpan().tag("error.mensaje", errors.toString());
		}catch(FeignException fe) {
			log.error(String.format("El usuario %s no existe en el sistema",authentication.getName()));
		}
		
	}

}
