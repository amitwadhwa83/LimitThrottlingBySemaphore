package com.messagebird.domain;

import java.math.BigInteger;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Message {

    @Digits(integer = 11, fraction = 0, message = "Should be less than 11 digits")
    private BigInteger recipient;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "Can only be a numeric telephone number or alphanumeric")
    private String originator;

    @NotNull
    private String message;

    public BigInteger getRecipient() {
	return recipient;
    }

    public void setRecipient(BigInteger recipient) {
	this.recipient = recipient;
    }

    public String getOriginator() {
	return originator;
    }

    public void setOriginator(String originator) {
	this.originator = originator;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 37).append(message).append(originator).append(recipient).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Message other = (Message) obj;

	return new EqualsBuilder().append(message, other.message).append(recipient, other.recipient)
		.append(recipient, other.recipient).isEquals();
    }
}