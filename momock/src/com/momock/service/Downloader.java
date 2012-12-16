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
package com.momock.service;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import com.momock.app.App;
import com.momock.net.HttpSession;

public class Downloader implements IDownloader {
	Timer timer = null;
	HttpSession executingSessions[];

	public Downloader() {
		this(5);
	}

	public Downloader(int maxTaskCount) {
		executingSessions = new HttpSession[maxTaskCount];
	}

	@Override
	public void start() {
	}

	void startTimer() {
		if (timer != null)
			return;
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				processQueue();
			}

		}, 1000, 1000);
	}

	void stopTimer() {
		if (timer != null){
			timer.cancel();
			timer = null;
		}
	}

	@Override
	public void stop() {
		stopTimer();
		for (int i = 0; i < executingSessions.length; i++) {
			HttpSession session = executingSessions[i];
			if (session != null) {
				session.stop();
				executingSessions[i] = null;
			}
		}
		queue.clear();
	}

	protected LinkedList<HttpSession> queue = new LinkedList<HttpSession>();

	@Override
	public HttpSession getSession(String url) {
		Iterator<HttpSession> it = queue.iterator();
		while (it.hasNext()) {
			HttpSession session = it.next();
			if (url.equals(session.getUrl())) {
				return session;
			}
		}
		for (int i = 0; i < executingSessions.length; i++) {
			HttpSession session = executingSessions[i];
			if (session != null && url.equals(session.getUrl())) {
				return session;
			}
		}
		return null;
	}

	void processQueue() {
		if (queue.isEmpty())
			stopTimer();
		else {
			for (int i = 0; i < executingSessions.length; i++) {
				HttpSession session = executingSessions[i];
				if (session != null) {
					if (session.isFinished())
						session = null;
				}
				if (session == null && !queue.isEmpty()) {
					session = queue.poll();
					executingSessions[i] = session;
					session.start();
				}
			}
		}
	}

	void resetTimer() {
		if (queue.isEmpty())
			stopTimer();
		else if (timer == null) {
			startTimer();
		}
	}

	@Override
	public synchronized void addSession(HttpSession session){
		if (!queue.contains(session)){
			queue.add(session);
			resetTimer();
		}
	}
	@Override
	public synchronized void removeSession(HttpSession session){
		if (queue.contains(session)){
			queue.remove(session);
		}
	}
	@Override
	public HttpSession newSession(String url, File file) {
		IHttpService httpService = App.get().getService(IHttpService.class);
		return new HttpSession(httpService.getHttpClient(), url, file);
	}

}