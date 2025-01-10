package config.videosystem.mixing;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/*
 * 본인은 Config가 없고 두가지 Config를 모아놓은 것
 * <---- JavaConfig2, JavaConfig1
 */
@Configuration
@Import({DVDConfig.class, DVDPlayerConfig.class})
public class VideoSystemConfig {

}
