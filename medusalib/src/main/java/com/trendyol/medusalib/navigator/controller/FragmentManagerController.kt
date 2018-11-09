package com.trendyol.medusalib.navigator.controller

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.trendyol.medusalib.common.extensions.commitAdd
import com.trendyol.medusalib.common.extensions.commitAttach
import com.trendyol.medusalib.common.extensions.commitDetach
import com.trendyol.medusalib.common.extensions.commitHide
import com.trendyol.medusalib.common.extensions.commitRemove
import com.trendyol.medusalib.common.extensions.commitShow
import com.trendyol.medusalib.navigator.OnNavigatorTransactionListener
import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction
import com.trendyol.medusalib.navigator.transaction.TransactionType

class FragmentManagerController(private val fragmentManager: FragmentManager,
                                private val containerId: Int,
                                private val navigatorTransaction: NavigatorTransaction) {

    fun enableFragment(fragmentTag: String) {
        val navigatorTransaction = getFragmentNavigatorTransaction(fragmentTag)

        with(fragmentManager) {
            when (navigatorTransaction.transactionType) {
                TransactionType.SHOW_HIDE -> commitShow(fragmentTag)
                TransactionType.ATTACH_DETACH -> commitAttach(fragmentTag)
            }
        }
    }

    fun disableFragment(fragmentTag: String) {
        val navigatorTransaction = getFragmentNavigatorTransaction(fragmentTag)

        with(fragmentManager) {
            when (navigatorTransaction.transactionType) {
                TransactionType.SHOW_HIDE -> commitHide(fragmentTag)
                TransactionType.ATTACH_DETACH -> commitDetach(fragmentTag)
            }
        }
    }

    fun removeFragment(fragmentTag: String) {
        fragmentManager.commitRemove(fragmentTag)
    }

    fun addFragment(fragment: Fragment, fragmentTag: String) {
        fragmentManager.commitAdd(containerId, fragment, fragmentTag)
    }

    fun disableAndStartFragment(disableFragmentTag: String, startFragment: Fragment, startFragmentTag: String) {
        val disabledFragment = fragmentManager.findFragmentByTag(disableFragmentTag)
        val fragmentTransaction = fragmentManager.beginTransaction()

        val disabledFragmentNavigatorTransaction = getFragmentNavigatorTransaction(disableFragmentTag)

        when (disabledFragmentNavigatorTransaction.transactionType) {
            TransactionType.SHOW_HIDE -> fragmentTransaction.hide(disabledFragment)
            TransactionType.ATTACH_DETACH -> fragmentTransaction.detach(disabledFragment)
        }

        fragmentTransaction.add(containerId, startFragment, startFragmentTag)
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun isFragmentNull(fragmentTag: String): Boolean {
        return fragmentManager.findFragmentByTag(fragmentTag) == null
    }

    private fun getFragment(fragmentTag: String): Fragment? {
        return fragmentManager.findFragmentByTag(fragmentTag)
    }

    private fun getFragmentNavigatorTransaction(fragmentTag: String): NavigatorTransaction {
        var navigatorTransaction = navigatorTransaction

        getFragment(fragmentTag)?.let {
            if (it is OnNavigatorTransactionListener) {
                navigatorTransaction = it.getNavigatorTransaction()
            }
        }

        return navigatorTransaction
    }
}