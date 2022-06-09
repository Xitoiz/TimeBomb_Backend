package fr.xitoiz.timebomb.emitter;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class MatchEmitter {
	private Integer userId;
	private Integer matchId;
	private SseEmitter sse = new SseEmitter();

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getMatchId() {
		return matchId;
	}

	public void setMatchId(Integer matchId) {
		this.matchId = matchId;
	}

	public SseEmitter getSse() {
		return sse;
	}

	public void setSse(SseEmitter sse) {
		this.sse = sse;
	}

	public MatchEmitter(Integer userId, Integer matchId) {
		this.userId = userId;
		this.matchId = matchId;
	}
}