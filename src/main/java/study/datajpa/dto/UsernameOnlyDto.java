package study.datajpa.dto;

public class UsernameOnlyDto {

    private final String username;

    public UsernameOnlyDto(String username) { // 생성자의 이름으로 파라미터 매칭
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
