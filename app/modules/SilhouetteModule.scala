package modules

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.crypto.{Crypter, CrypterAuthenticatorEncoder}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings}
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.util.{DefaultFingerprintGenerator, PlayCacheLayer, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import models.daos.{PasswordInfoDAO, UserDAO, UserDAOImpl}
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WSClient
import services.{UserService, UserServiceImpl}
import utils.auth.DefaultEnv

/**
  * Класс SilhouetteModule.
  * -----------------------
  *
  * Модуль для Silhouette.
  *
  * @author EMurzakaev@it.ru.
  */
class SilhouetteModule extends AbstractModule with ScalaModule {

    /**
      * КОнфигурация модуля.
      */
    def configure() {
        bind[UserService].to[UserServiceImpl]
        bind[UserDAO].to[UserDAOImpl]
        bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
        bind[CacheLayer].to[PlayCacheLayer]
        bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
        bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
        bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
        bind[EventBus].toInstance(EventBus())
        bind[Clock].toInstance(Clock())
        bind[DelegableAuthInfoDAO[PasswordInfo]].to[PasswordInfoDAO]
    }

    /**
      * Provides the HTTP layer implementation.
      *
      * @param client Play's WS client.
      * @return The HTTP layer implementation.
      */
    @Provides
    def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

    /**
      * Provides the Silhouette environment.
      *
      * @param userService          The user service implementation.
      * @param authenticatorService The authentication service implementation.
      * @param eventBus             The event bus instance.
      * @return The Silhouette environment.
      */
    @Provides
    def provideEnvironment(
                                  userService: UserService,
                                  authenticatorService: AuthenticatorService[JWTAuthenticator],
                                  eventBus: EventBus): Environment[DefaultEnv] = {

        Environment[DefaultEnv](
            userService,
            authenticatorService,
            Seq(),
            eventBus
        )
    }

    /**
      * Provides the crypter for the authenticator.
      *
      * @param configuration The Play configuration.
      * @return The crypter for the authenticator.
      */
    @Provides
    @Named("authenticator-crypter")
    def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
        val config = configuration.underlying.getConfig("silhouette.authenticator.crypter")
        new JcaCrypter(JcaCrypterSettings("play"))
    }

    /**
      * Provides the auth info repository.
      *
      * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
      * @return The auth info repository instance.
      */
    @Provides
    def provideAuthInfoRepository(passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository = {
        new DelegableAuthInfoRepository(passwordInfoDAO)
    }

    /**
      * Provides the authenticator service.
      *
      * @param crypter       The crypter implementation.
      * @param idGenerator   The ID generator implementation.
      * @param configuration The Play configuration.
      * @param clock         The clock instance.
      * @return The authenticator service.
      */
    @Provides
    def provideAuthenticatorService(
                                           @Named("authenticator-crypter") crypter: Crypter,
                                           idGenerator: IDGenerator,
                                           configuration: Configuration,
                                           clock: Clock): AuthenticatorService[JWTAuthenticator] = {
        val config = configuration.underlying.getConfig("silhouette.authenticator")

        val encoder = new CrypterAuthenticatorEncoder(crypter)
        new JWTAuthenticatorService(JWTAuthenticatorSettings(sharedSecret = "1"), None, encoder, idGenerator, clock)
    }

    /**
      * Provides the password hasher registry.
      *
      * @param passwordHasher The default password hasher implementation.
      * @return The password hasher registry.
      */
    @Provides
    def providePasswordHasherRegistry(passwordHasher: PasswordHasher): PasswordHasherRegistry = PasswordHasherRegistry(passwordHasher)

    /**
      * Provides the credentials provider.
      *
      * @param authInfoRepository     The auth info repository implementation.
      * @param passwordHasherRegistry The password hasher registry.
      * @return The credentials provider.
      */
    @Provides
    def provideCredentialsProvider(
                                          authInfoRepository: AuthInfoRepository,
                                          passwordHasherRegistry: PasswordHasherRegistry): CredentialsProvider = {

        new CredentialsProvider(authInfoRepository, passwordHasherRegistry)
    }

}
