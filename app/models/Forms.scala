package models

import play.api.libs.json.{Json, OFormat}

/**
  * The form data.
  *
  * @param firstName The first name of a user.
  * @param lastName  The last name of a user.
  * @param email     The email of the user.
  * @param password  The password of the user.
  */
case class SignUpForm(firstName: String, lastName: String, email: String, password: String)

/**
  * The form data.
  *
  * @param email      The email of the user.
  * @param password   The password of the user.
  * @param rememberMe Indicates if the user should stay logged in on the next visit.
  */
case class LoginForm(email: String, password: String, rememberMe: Boolean)

/**
  * Объект-комнаьюн SignUpForm.
  *
  * Содержит формат для case класса SignUpForm.
  */
object SignUpForm {
    implicit val signUpFormFormat: OFormat[SignUpForm] = Json.format[SignUpForm]
}

/**
  * Объект-комнаьюн LoginForm.
  *
  * Содержит формат для case класса LoginForm.
  */
object LoginForm {
    implicit val loginFormFormat: OFormat[LoginForm] = Json.format[LoginForm]
}