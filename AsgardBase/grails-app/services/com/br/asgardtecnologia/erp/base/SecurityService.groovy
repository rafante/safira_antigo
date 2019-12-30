package com.br.asgardtecnologia.erp.base

import com.br.asgardtecnologia.erp.security.Perfil
import com.br.asgardtecnologia.erp.security.Requestmap
import com.br.asgardtecnologia.erp.security.Usuario
import com.br.asgardtecnologia.erp.security.UsuarioPerfil
import org.codehaus.groovy.grails.commons.DefaultGrailsControllerClass
import org.springframework.transaction.annotation.Transactional

public class SecurityService extends BaseService {

    static ROLE_ADMIN = 'ROLE_ADMIN'
    static REQUEST_MAP_IRRELEVANT_ACTIONS = [null, '']
    static REQUEST_MAP_IRRELEVANT_CONTROLLERS = [null, '', 'login', 'logout', 'index', 'jasper', 'jasperDemo', 'dbdoc',
                                                 'javascript', 'grid', 'autoComplete', 'requestmap', 'license']
    def grailsApplication

    def init() {
        if (!Usuario.count()) {
            println "Criando o Deus Admin ..."

            def adminPerfil = new Perfil(authority: 'ROLE_ADMIN', dinamico: true).save(flush: true)
            def userPerfil = new Perfil(authority: 'ROLE_USER', dinamico: true).save(flush: true)

            def adminUsuario = new Usuario(username: 'admin', enabled: true, password: 'admin')
            adminUsuario.save(flush: true)

            UsuarioPerfil.create(adminUsuario, adminPerfil, true)
            UsuarioPerfil.create(adminUsuario, userPerfil, true)

            println "... Deus Admin criado"
        }

        if (!ParametrosGerais.count()) (new ParametrosGerais()).save()

        preparaPerfis()
        //TODO: Transitory - only for tests

    }

    @Transactional
    def preparaPerfis() {
        println "Preparando Yggdrasil ..."

        if (!Perfil.countByAuthority('ROLE_ADMIN')) new Perfil(authority: 'ROLE_ADMIN', dinamico: false).save(flush: true)
        if (!Perfil.countByAuthority('ROLE_USER')) new Perfil(authority: 'ROLE_USER', dinamico: false).save(flush: true)

        // Adiciona o atributo IS_AUTHENTICATED_FULLY para a raiz
        if (!Requestmap.countByUrl('/')) (new Requestmap(configAttribute: 'IS_AUTHENTICATED_FULLY', url: '/')).save(flush: true)

        // Varre os controllers/URIs
        for (DefaultGrailsControllerClass controller in grailsApplication.controllerClasses) {
            for (uri in controller.getURIs()) {
                // Determina o CONFIGATTRIBUTE
                String configAttribute = getConfigAttributeName(uri)

                // Verifica se é necessário criar um Requestmap para a URI atual
                def splittedURI = splitConfigAttribute(configAttribute)
                if (isRequestmapNecessary(splittedURI)) {
                    // Verifica se a URI já está gravada
                    if (!Requestmap.countByUrl(uri)) {
                        (new Perfil(authority: configAttribute, dinamico: true)).save(flush: true) // Cria o Perfil
                        (new Requestmap(configAttribute: 'ROLE_ADMIN, ' + configAttribute, url: uri)).save(flush: true)
                    }
                } else {
                    if (splittedURI.action && !(splittedURI.controller in REQUEST_MAP_IRRELEVANT_CONTROLLERS))
                        println "-Caminho " + uri + " considerado irrelevante por Heimdall. Não haverá controle de acesso para tal."
                }
            }
        }

        // Cria os acessos para os widgets
        for (Widget widget in Widget.list()) {
            if (!Requestmap.countByUrl(widget.template)) {
                (new Perfil(authority: "ROLE_" + widget.name, dinamico: true)).save(flush: true) // Cria o Perfil
                (new Requestmap(configAttribute: 'ROLE_ADMIN, ' + "ROLE_" + widget.name, url: widget.template)).save(flush: true)
            }
        }

        println "... Yggdrasil preparada"
    }

    String getConfigAttributeName(String uri) {
        String configAttribute = uri.replaceAll("\\*", "")
        configAttribute = "ROLE_" + configAttribute.split('/').findAll({ it }).join('_')

        return configAttribute
    }

    def splitConfigAttribute(configAttribute) {
        def perfilSplitted = configAttribute.split("_")

        def splittedConfigAttribute = [:]
        splittedConfigAttribute.controller = perfilSplitted[1]
        splittedConfigAttribute.action = perfilSplitted.size() > 2 ? perfilSplitted[2] : null

        return splittedConfigAttribute
    }

    Boolean isRequestmapNecessary(Map splittedURI) {
        return (!(splittedURI.controller in REQUEST_MAP_IRRELEVANT_CONTROLLERS) &&
                !(splittedURI.action in REQUEST_MAP_IRRELEVANT_ACTIONS))
    }

    def isAdmin(usuario) {
        return (UsuarioPerfil.countByUsuarioAndPerfil(usuario, Perfil.findByAuthority(ROLE_ADMIN)))
    }

    def saveUsuarioPerfil(Usuario usuario, list, Boolean isAdmin) {
        // Apaga os perfis do usuário atual
        UsuarioPerfil.findAllByUsuario(usuario).each {
            it.delete(flush: true)
        }

        if (isAdmin) {
            (new UsuarioPerfil(usuario: usuario, perfil: Perfil.findByAuthority(ROLE_ADMIN))).save(flush: true)
        }

        list.each { obj ->
            obj.each {
                if (it.value != null && it.value != "" && it.value && it.value != "false") {
                    String authority = "ROLE_" + obj.controller + "_" + it.key
                    Perfil perfil = Perfil.findByAuthority(authority)
                    if (perfil) {
                        (new UsuarioPerfil(usuario: usuario, perfil: perfil)).save(flush: true)
                    }
                }
            }
        }

    }

}