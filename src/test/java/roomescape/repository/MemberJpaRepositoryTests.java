package roomescape.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import roomescape.domain.Member;
import roomescape.domain.MemberRole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberJpaRepositoryTests {

	@Autowired
	private MemberJpaRepository memberJpaRepository;

	@Test
	void isExistsWhenMemberExists() {
		// given
		Member member = Member.builder()
			.id(1L)
			.name("tester")
			.email("tester@gmail.com")
			.password("encodedPassword")
			.role(MemberRole.USER.name())
			.build();
		this.memberJpaRepository.save(member);

		// when
		boolean exists = this.memberJpaRepository.existsByName("tester");

		// then
		assertThat(exists).isTrue();
	}

	@Test
	void isExistsWhenMemberDoesNotExist() {

		// given, when
		boolean exists = this.memberJpaRepository.existsByName("테스터");

		// then
		assertThat(exists).isFalse();
	}

	@Test
	void save() {
		// given
		Member member = Member.builder()
			.name("테스터")
			.email("tester@naver.com")
			.password("encodedPassword")
			.role(MemberRole.USER.name())
			.build();

		// when
		Member savedMember = this.memberJpaRepository.save(member);

		// then
		SoftAssertions.assertSoftly((softly) -> {
			softly.assertThat(savedMember).isNotNull();
			softly.assertThat(savedMember.getName()).isEqualTo("테스터");
			softly.assertThat(savedMember.getEmail()).isEqualTo("tester@naver.com");
			softly.assertThat(savedMember.getRole()).isEqualTo(MemberRole.USER.name());
		});
	}

	@Test
	void findByEmailWhenMemberExists() {
		// given
		Member member = Member.builder()
			.name("tester")
			.email("tester@naver.com")
			.password("encodedPassword")
			.role(MemberRole.USER.name())
			.build();
		this.memberJpaRepository.save(member);

		// when
		Optional<Member> foundMember = this.memberJpaRepository.findByEmail("tester@naver.com");

		// then
		assertThat(foundMember).isPresent();
		foundMember.ifPresent((m) -> {
			assertThat(m.getName()).isEqualTo("tester");
			assertThat(m.getEmail()).isEqualTo("tester@naver.com");
			assertThat(m.getRole()).isEqualTo(MemberRole.USER.name());
		});
	}

	@Test
	void findByEmailWhenMemberDoesNotExist() {
		// given
		String email = "tester@naver.com";

		// when
		Optional<Member> foundMember = this.memberJpaRepository.findByEmail(email);

		// then
		SoftAssertions.assertSoftly((softly) -> {
			softly.assertThat(foundMember).isNotNull();
			softly.assertThat(foundMember.isPresent()).isFalse();
		});
	}

	@Test
	void findById() {
		// given
		long id = 1L;

		// when
		Optional<Member> foundMember = this.memberJpaRepository.findById(id);

		// then
		SoftAssertions.assertSoftly((softly) -> {
			softly.assertThat(foundMember).isNotNull();
			softly.assertThat(foundMember.isPresent()).isTrue();
			foundMember.ifPresent((m) -> {
				softly.assertThat(m.getName()).isEqualTo("tester");
				softly.assertThat(m.getEmail()).isEqualTo("tester@gmail.com");
				softly.assertThat(m.getRole()).isEqualTo(MemberRole.USER.name());
			});
		});

	}

	@Test
	void findByRole() {
		// given, when
		List<Member> members = this.memberJpaRepository.findByRole(MemberRole.USER.name());

		// then
		assertThat(members).isNotNull();
		assertThat(members).hasSize(1);
	}

}
