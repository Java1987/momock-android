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

public abstract class FragmentContainerHolder implements IComponentHolder{
	public abstract int getFragmentContainerId();
	public abstract FragmentManager getFragmentManager();
	public abstract FragmentActivity getAcivity();
	
	public static FragmentContainerHolder get(FragmentActivity activity, final int id)
	{
		final WeakReference<FragmentActivity> refActivity = new WeakReference<FragmentActivity>(activity);
		return new FragmentContainerHolder(){

			@Override
			public int getFragmentContainerId() {
				return id;
			}

			@Override
			public FragmentManager getFragmentManager() {
				return getAcivity() == null ? null : getAcivity().getSupportFragmentManager();
			}

			@Override
			public FragmentActivity getAcivity() {
				return refActivity == null ? null : refActivity.get();
			}			
		};
	}
}