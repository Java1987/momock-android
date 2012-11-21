/*******************************************************************************
 * Copyright 2012 momock.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.momock.holder;

import java.lang.ref.WeakReference;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class FragmentContainerHolder implements IComponentHolder{
	int fragmentContainerId;
	WeakReference<FragmentManager> refFragmentManager;
	public int getFragmentContainerId()	{
		return fragmentContainerId;
	}
	public FragmentManager getFragmentManager(){
		return refFragmentManager.get();
	}
	
	public FragmentContainerHolder(FragmentActivity activity, int fragmentContainerId)
	{
		this.fragmentContainerId = fragmentContainerId;
		refFragmentManager = new WeakReference<FragmentManager>(activity.getSupportFragmentManager());
	}

	public FragmentContainerHolder(FragmentManager fm, int fragmentContainerId)
	{
		this.fragmentContainerId = fragmentContainerId;
		refFragmentManager = new WeakReference<FragmentManager>(fm);
	}
}
