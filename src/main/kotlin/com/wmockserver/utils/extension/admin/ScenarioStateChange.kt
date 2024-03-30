package com.wmockserver.utils.extension.admin

import com.github.tomakehurst.wiremock.admin.AdminTask
import com.github.tomakehurst.wiremock.admin.Router
import com.github.tomakehurst.wiremock.common.url.PathParams
import com.github.tomakehurst.wiremock.core.Admin
import com.github.tomakehurst.wiremock.extension.AdminApiExtension
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.http.ResponseDefinition
import com.github.tomakehurst.wiremock.stubbing.ServeEvent

class ScenarioStateChange : AdminApiExtension {
    override fun getName(): String = "scenario-state-change-request"

    override fun contributeAdminApiRoutes(router: Router) {
        router.add(RequestMethod.POST, "/scenarios/reset", ScenarioStateChangeTask::class.java)
    }
}

class ScenarioStateChangeTask : AdminTask {
    override fun execute(admin: Admin?, serveEvent: ServeEvent?, pathParams: PathParams?): ResponseDefinition {
        TODO("Not yet implemented")
    }
}