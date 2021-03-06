package com.xushiwei.kotlintest.mvp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hazz.kotlinmvp.mvp.model.bean.TabInfoBean

import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils

import com.xushiwei.kotlintest.di.component.DaggerHotComponent
import com.xushiwei.kotlintest.di.module.HotModule
import com.xushiwei.kotlintest.mvp.contract.HotContract
import com.xushiwei.kotlintest.mvp.presenter.HotPresenter

import com.xushiwei.kotlintest.R
import com.xushiwei.kotlintest.mvp.ui.adapter.MyTabLayoutAdapter
import kotlinx.android.synthetic.main.fragment_hot.*
import javax.inject.Inject

class HotFragment : BaseFragment<HotPresenter>(), HotContract.View {

    @Inject
    lateinit var mFragments: ArrayList<Fragment>
    @Inject
    lateinit var mTitles: ArrayList<String>

    companion object {
        fun newInstance(param: String): HotFragment {
            val fragment = HotFragment()
            val bundle = Bundle()
            bundle.putString("param", param)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerHotComponent //如找不到该类,请编译一下项目
            .builder()
            .appComponent(appComponent)
            .hotModule(HotModule(this))
            .build()
            .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_hot, container, false);
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun setData(data: Any?) {

    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {

    }

    override fun setTabInfo(tabInfoBean: TabInfoBean) {
        tabInfoBean.tabInfo.tabList.mapTo(mTitles) { it.name }
        tabInfoBean.tabInfo.tabList.mapTo(mFragments) { RankFragment.newInstance(it.apiUrl) }

        mViewPager.adapter = MyTabLayoutAdapter(childFragmentManager, mFragments, mTitles)
        mTabLayout.setupWithViewPager(mViewPager)
        mViewPager.offscreenPageLimit = 3
        mViewPager.currentItem = 0
    }
}
