package com.nbh.edushare.modules.auth.refreshtoken;

import org.springframework.data.jpa.repository.JpaRepository;

interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

}
