package com.jjbacsa.jjbacsabackend.etc.dto.slack;

public class SlackTarget {
    private final String webHookUrl;
    private final String channel;

    public SlackTarget(String webHookUrl, String channel) {
        this.webHookUrl = webHookUrl;
        this.channel = channel;
    }

	public String getWebHookUrl() {
		return webHookUrl;
	}

	public String getChannel() {
		return channel;
	}

}
