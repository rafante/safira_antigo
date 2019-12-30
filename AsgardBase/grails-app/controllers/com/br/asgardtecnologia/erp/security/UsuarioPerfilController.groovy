package com.br.asgardtecnologia.erp.security

import com.br.asgardtecnologia.erp.base.BaseController

import java.util.regex.Pattern

class UsuarioPerfilController extends BaseController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: ['GET', 'POST']]

    def hiddenActionButtons = ['create', 'show', 'delete', 'list']

    def securityService

    def index() {
        redirect action: 'edit', params: params
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def controllers = []
                def actions = ['index', 'list', 'show', 'create', 'edit']

                def perfilList = Perfil.findAllByDinamico(true)
                for (perfil in perfilList) {
                    def perfilSplitted = perfil.authority.split("_")
                    def action = perfilSplitted.size() > 2 ? perfilSplitted[2] : null
                    if (!action) continue

                    if (securityService.REQUEST_MAP_IRRELEVANT_CONTROLLERS.find({ it == perfilSplitted[1] })) continue

                    def controller = controllers.find({ it.name == perfilSplitted[1] })
                    if (controller) {
                        if (perfilSplitted.size() > 2) controller.actions << action
                    } else {
                        controller = [name: perfilSplitted[1], actions: [action]]
                    }

                    actions << action
                    controllers << controller
                }

                actions.unique()
                controllers.unique { a, b -> a.name <=> b.name }

                def instance = [:]
                instance["editableFields"] = "controller;" + actions.unique().join(";")
                instance["fields"] = "controllerText;" + actions.unique().join(";") + ";controller[HIDDEN]" // TODO! Ajustar o HIDDEN no GRID
                instance["usuario"] = Usuario.get(params.id)
                instance["isAdmin"] = securityService.isAdmin(instance["usuario"])
                instance["perfis"] = []

                Integer i = 1
                for (controller in controllers) {
                    def row = [id: i, controller: controller.name, controllerText: message(code: controller.name + ".label")]

                    for (action in controller.actions) {
                        String authority = "ROLE_" + controller.name + "_" + action

                        def perfil = Perfil.findByAuthority(authority)
                        def usuarioPerfil = UsuarioPerfil.findByUsuarioAndPerfil(instance["usuario"], perfil)

                        Boolean checked = (usuarioPerfil != null)

                        row[action] = checked
                    }

                    instance.perfis << row

                    i++
                }

                instance.perfis = instance.perfis.sort { a, b -> a.controllerText <=> b.controllerText }

                [instance: instance]
                break
            case 'POST':
                def list = []
                params.each { i, obj ->
                    if (((Pattern) ~/(perfil)\[[0-9]+\]/).matcher(i).matches()) {
                        list << obj
                    }
                }

                Boolean isAdmin = (params.isAdmin != null && params.isAdmin && params.isAdmin != "false")
                securityService.saveUsuarioPerfil(Usuario.get(params.usuario.id), list, isAdmin)

                redirect action: 'edit', id: params.usuario.id

                break
        }
    }

}
