package com.xushiwei.kotlintest.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.hazz.kotlinmvp.mvp.model.bean.TabInfoBean
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel

import com.jess.arms.di.scope.FragmentScope
import javax.inject.Inject

import com.xushiwei.kotlintest.mvp.contract.HotContract
import com.xushiwei.kotlintest.mvp.model.api.service.ApiService
import io.reactivex.Observable


@FragmentScope
class HotModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), HotContract.Model {

    override fun getTabInfoBean(): Observable<TabInfoBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java).getRankList()
    }

    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    override fun onDestroy() {
        super.onDestroy()
    }
}
