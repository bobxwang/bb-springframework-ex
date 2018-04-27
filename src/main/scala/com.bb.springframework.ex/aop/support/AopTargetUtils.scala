package com.bb.springframework.ex.aop.support

import org.springframework.aop.support.AopUtils
import org.springframework.aop.framework.AdvisedSupport
import org.springframework.aop.framework.AopProxy

object AopTargetUtils {

  @throws[Exception]
  def getTarget(proxy: Object): Object = {

    if (!AopUtils.isAopProxy(proxy)) {
      return proxy
    }

    return if (AopUtils.isCglibProxy(proxy)) getCglibProxyTargetObject(proxy) else getJdkDynamicProxyTargetObject(proxy)
  }

  @throws[Exception]
  private def getCglibProxyTargetObject(proxy: Any) = {
    val h = proxy.getClass.getDeclaredField("CGLIB$CALLBACK_0")
    h.setAccessible(true)
    val dynamicAdvisedInterceptor = h.get(proxy)
    val advised = dynamicAdvisedInterceptor.getClass.getDeclaredField("advised")
    advised.setAccessible(true)
    val target = advised.get(dynamicAdvisedInterceptor).asInstanceOf[AdvisedSupport].getTargetSource.getTarget
    target
  }

  @throws[Exception]
  private def getJdkDynamicProxyTargetObject(proxy: Any) = {
    val h = proxy.getClass.getSuperclass.getDeclaredField("h")
    h.setAccessible(true)
    val aopProxy = h.get(proxy).asInstanceOf[AopProxy]
    val advised = aopProxy.getClass.getDeclaredField("advised")
    advised.setAccessible(true)
    val target = advised.get(aopProxy).asInstanceOf[AdvisedSupport].getTargetSource.getTarget
    target
  }
}