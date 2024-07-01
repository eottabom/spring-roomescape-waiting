package roomescape.service;

import java.util.List;
import java.util.stream.Collectors;

import roomescape.domain.Member;
import roomescape.domain.MemberRole;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.MemberJpaRepository;
import roomescape.web.controller.dto.LoginRequest;
import roomescape.web.controller.dto.MemberRequest;
import roomescape.web.controller.dto.MemberResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

	private final MemberJpaRepository memberJpaRepository;

	private final PasswordEncoder passwordEncoder;

	MemberService(MemberJpaRepository memberJpaRepository, PasswordEncoder passwordEncoder) {
		this.memberJpaRepository = memberJpaRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public MemberResponse create(MemberRequest request) {
		var encodedPassword = this.passwordEncoder.encode(request.password());
		var member = Member.builder().name(request.name()).email(request.email()).password(encodedPassword).build();

		var isExists = this.memberJpaRepository.existsByName(request.name());
		if (isExists) {
			throw new RoomEscapeException(ErrorCode.DUPLICATE_MEMBER);
		}
		var savedMember = this.memberJpaRepository.save(member);
		return MemberResponse.from(savedMember);
	}

	@Transactional(readOnly = true)
	public MemberResponse findMemberByLoginRequest(LoginRequest request) {
		var foundMember = this.memberJpaRepository.findByEmail(request.email())
			.orElseThrow(() -> new RoomEscapeException(ErrorCode.NOT_FOUND_MEMBER));

		checkPassword(request.password(), foundMember.getPassword());
		return MemberResponse.from(foundMember);
	}

	@Transactional(readOnly = true)
	public List<MemberResponse> findAllMembersViaRoleUser() {
		return this.memberJpaRepository.findByRole(MemberRole.USER.name())
			.stream()
			.map((member) -> new MemberResponse(member.getId(), member.getName(), member.getEmail(), member.getRole()))
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Member findById(Long id) {
		return this.memberJpaRepository.findById(id)
			.orElseThrow(() -> new RoomEscapeException(ErrorCode.NOT_FOUND_MEMBER));
	}

	private void checkPassword(String inputPassword, String storedPassword) {
		if (!this.passwordEncoder.matches(inputPassword, storedPassword)) {
			throw new RoomEscapeException(ErrorCode.INVALID_PASSWORD);
		}
	}

}
