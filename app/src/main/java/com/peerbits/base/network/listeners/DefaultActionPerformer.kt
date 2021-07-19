package com.peerbits.base.network.listeners

import java.util.HashMap

/**
 * Created by Deep Patel on 8/1/2017
 */

interface DefaultActionPerformer {
    fun onActionPerform(headers: HashMap<String, String>, params: HashMap<String, String>)
}
