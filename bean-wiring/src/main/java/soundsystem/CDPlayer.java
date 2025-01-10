package soundsystem;

import org.springframework.stereotype.Component;

@Component
public class CDPlayer {
	private final CompactDisc cd;
	
	// DI : 생성자 wiring
	public CDPlayer(CompactDisc cd) {
		this.cd = cd;
	}
	
	public String play() {
		return cd.play();
	}
}
