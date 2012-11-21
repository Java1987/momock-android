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
package com.momock.app;

import java.util.HashMap;

import junit.framework.Assert;

import com.momock.outlet.IOutlet;
import com.momock.outlet.IPlug;
import com.momock.outlet.PlaceholderOutlet;
import com.momock.util.Logger;


public abstract class Case implements ICase {
	String name;
	public Case(){
		name = getClass().getName();
	}
	public Case(String name)
	{
		this.name = name;
	}
	public Case(ICase parent){
		this.parent = parent;
		this.name = getClass().getName();
	}

	public Case(ICase parent, String name){
		this.parent = parent;
		this.name = name;
	}
	public String getName(){
		return name;
	}
	
	public String getFullName(){
		return getParent() != null ? getParent().getFullName() + "/" + name : "/" + name;
	}
	
	public abstract void onCreate();
	
	@Override
	public void onActivate() {
		
	}
	@Override
	public void onDeactivate() {
		
	}
	@Override
	public void run(Object... args) {
		
	}

	// IAttachable implementation
	Object attachedObject = null;
	@Override
	public Object getAttachedObject() {
		return attachedObject;
	}

	@Override
	public void attach(Object target) {
		detach();
		if (target != null)
		{
			attachedObject = target;	
			onAttach(target);
		}
	}
	
	@Override
	public void detach(){
		if (getAttachedObject() != null)
		{
			onDetach(attachedObject);
			attachedObject = null;
		}
	}
	
	@Override
	public void onAttach(Object target)
	{
		
	}
	@Override
	public void onDetach(Object target)
	{
		
	}

	// Implementation for ICase interface
	protected ICase activeCase = null;
	protected ICase parent;
	protected HashMap<String, ICase> cases = new HashMap<String, ICase>();

	@Override
	public ICase getParent() {
		return parent;
	}
	
	@Override
	public ICase getActiveCase() {
		return activeCase;
	}

	@Override
	public void setActiveCase(ICase kase) {
		if (activeCase != kase) {
			if (activeCase != null)
				activeCase.onDeactivate();
			activeCase = kase;
			if (activeCase != null)
				activeCase.onActivate();
		}
	}
	
	@Override
	public ICase getCase(String name) {
		Assert.assertNotNull(name);
		ICase kase = null;
		int pos = name.indexOf('/');
		if (pos == -1){
			kase = cases.get(name);			
		} else {
			if (name.startsWith("/"))
				kase = App.get().getCase(name);
			else{
				kase = cases.get(name.substring(0, pos));
				if (kase != null)
					kase = kase.getCase(name.substring(pos + 1));
			}
		}
		return kase;
	}

	@Override
	public void addCase(ICase kase){
		if (!cases.containsKey(kase.getName()))
		{
			cases.put(kase.getName(), kase);
			kase.onCreate();
		}
	}
	
	@Override
	public void removeCase(String name) {
		if (cases.containsKey(name))
			cases.remove(name);
	}

	@SuppressWarnings("rawtypes")
	HashMap<String, IOutlet> outlets = new HashMap<String, IOutlet>(); 

	@SuppressWarnings({ "unchecked" })
	@Override
	public <P extends IPlug, T> IOutlet<P, T> getOutlet(String name) {
		IOutlet<P, T> outlet = null;
		if (outlets.containsKey(name))
			outlet = outlets.get(name);
		if (outlet == null)
			outlet = (IOutlet<P, T>)(getParent() == null ? App.get().getOutlet(name) : getParent().getOutlet(name));
		if (outlet == null)
		{
			outlet = new PlaceholderOutlet<P, T>();
			outlets.put(name, outlet);
		}
		return outlet;
	}

	@Override
	public  <P extends IPlug, T> void addOutlet(String name, IOutlet<P, T> outlet) {
		Logger.debug("addOutlet : " + name);
		if (outlets.containsKey(name) && outlet != null)
		{
			IOutlet<?, ?> oldOutlet = outlets.get(name);
			if (oldOutlet instanceof PlaceholderOutlet)
				((PlaceholderOutlet<?, ?>)oldOutlet).transfer(outlet);
		}
		if (outlet == null)
			outlets.remove(name);
		else
			outlets.put(name, outlet);
	}

	@Override
	public void removeOutlet(String name) {
		if (outlets.containsKey(name))
		{
			outlets.remove(name);
		}
	}
}
