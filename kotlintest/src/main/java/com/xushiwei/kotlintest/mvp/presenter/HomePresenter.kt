package com.xushiwei.kotlintest.mvp.presenter

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import com.hazz.kotlinmvp.mvp.model.bean.HomeBean

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.utils.RxLifecycleUtils
import com.xushiwei.kotlintest.R
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import com.xushiwei.kotlintest.mvp.contract.HomeContract
import com.xushiwei.kotlintest.mvp.ui.adapter.HomeListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import me.jessyan.rxerrorhandler.handler.RetryWithDelay

@FragmentScope
class HomePresenter
@Inject
constructor(model: HomeContract.Model, rootView: HomeContract.View) :
    BasePresenter<HomeContract.Model, HomeContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager

    @Inject
    lateinit var mAdapter: HomeListAdapter

    private var bannerHomeBean: HomeBean? = null

    private var nextPageUrl: String? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onCreate() {
        getHomeList(true, 1)
    }

     fun getHomeList(pullToRefresh: Boolean, num: Int) {
         mModel.getHomeBean(num)
            .subscribeOn(Schedulers.io())
            .retryWhen(RetryWithDelay(2, 2))
            .doOnSubscribe {
                if (pullToRefresh) {
                    mRootView.showLoading()
                }
            }
            .flatMap { homeBean ->
                val bannerItemList = homeBean.issueList[0].itemList
                bannerItemList.filter { item ->
                    item.type == "banner2" || item.type == "horizontalScrollCard"
                }.forEach { item ->
                    bannerItemList.remove(item)
                }
                bannerHomeBean = homeBean
                mModel.getmoreData(homeBean.nextPageUrl)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
            .doFinally {
                if (pullToRefresh){
                    mRootView.hideLoading()
                }
            }
            .subscribe(object : ErrorHandleSubscriber<HomeBean?>(mErrorHandler) {

                override fun onError(t: Throwable) {
                    super.onError(t)
                    if (pullToRefresh) {
                        mAdapter.setEnableLoadMore(true)
                        mRootView.hideLoading()
                    } else {
                        mAdapter.loadMoreFail()
                    }
                    mAdapter.setEmptyView(R.layout.layout_error_view)
                }

                override fun onNext(t: HomeBean) {
                    if (pullToRefresh) {
                        nextPageUrl = t.nextPageUrl
                        //过滤掉 Banner2(包含广告,等不需要的 Type), 具体查看接口分析
                        val newBannerItemList = t.issueList[0].itemList
                        newBannerItemList.filter { item ->
                            item.type == "banner2" || item.type == "horizontalScrollCard"
                        }.forEach { item ->
                            //移除 item
                            newBannerItemList.remove(item)
                        }
                        // 重新赋值 Banner 长度
                        bannerHomeBean!!.issueList[0].count = bannerHomeBean!!.issueList[0].itemList.size
                        //赋值过滤后的数据 + banner 数据
                        bannerHomeBean?.issueList!![0].itemList.addAll(newBannerItemList)

                        mAdapter.setNewData(bannerHomeBean!!.issueList[0].itemList)
                        mAdapter.setBannerSize(bannerHomeBean!!.issueList[0].count)
                        mAdapter.setEnableLoadMore(true)
                        mRootView.hideLoading()
                        mAdapter.disableLoadMoreIfNotFullPage()
                    } else {
                        getMoreData()
                    }
                }
            })
    }

    private fun getMoreData() {
        mModel.getmoreData(nextPageUrl!!)
            .subscribeOn(Schedulers.io())
            .retryWhen(RetryWithDelay(2, 2))
            .observeOn(AndroidSchedulers.mainThread())
            .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
            .subscribe(object : ErrorHandleSubscriber<HomeBean?>(mErrorHandler) {

                override fun onError(t: Throwable) {
                    super.onError(t)
                    mAdapter.loadMoreFail()
                }

                override fun onNext(homeBean: HomeBean) {
                    val newItemList = homeBean.issueList[0].itemList
                    newItemList.filter { item ->
                        item.type == "banner2" || item.type == "horizontalScrollCard"
                    }.forEach { item ->
                        //移除 item
                        newItemList.remove(item)
                    }
                    nextPageUrl = homeBean.nextPageUrl
                    mAdapter.addData(newItemList)
                    mAdapter.loadMoreComplete()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
