package com.xushiwei.kotlintest.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.xushiwei.kotlintest.di.module.RankModule

import com.jess.arms.di.scope.FragmentScope
import com.xushiwei.kotlintest.mvp.ui.fragment.RankFragment


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 05/23/2019 18:12
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
@Component(modules = arrayOf(RankModule::class), dependencies = arrayOf(AppComponent::class))
interface RankComponent {
    fun inject(fragment: RankFragment)
}
