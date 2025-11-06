package one.microstream.bsr.domain;

import java.util.List;
import java.util.Objects;

import one.microstream.bsr.dto.AuthorDto;

public class Author
{
	private long id;
	private String email;
	private String firstname;
	private String lastname;
	private List<Address> addresses;

	public Author(
		final long id,
		final String email,
		final String firstname,
		final String lastname,
		final List<Address> addresses
	)
	{
		this.id = id;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.addresses = addresses;
	}

	public Author(final AuthorDto author)
	{
		this(
			author.id(),
			author.email(),
			author.firstname(),
			author.lastname(),
			author.addresses().stream().map(Address::new).toList()
		);
	}

	public String getFullName()
	{
		return "%s %s".formatted(this.firstname, this.lastname);
	}

	public long getId()
	{
		return this.id;
	}

	public void setId(final long id)
	{
		this.id = id;
	}

	public String getEmail()
	{
		return this.email;
	}

	public void setEmail(final String email)
	{
		this.email = email;
	}

	public String getFirstname()
	{
		return this.firstname;
	}

	public void setFirstname(final String firstname)
	{
		this.firstname = firstname;
	}

	public String getLastname()
	{
		return this.lastname;
	}

	public void setLastname(final String lastname)
	{
		this.lastname = lastname;
	}

	public List<Address> getAddresses()
	{
		return this.addresses;
	}

	public void setAddresses(final List<Address> addresses)
	{
		this.addresses = addresses;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.addresses, this.email, this.firstname, this.lastname);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (this.getClass() != obj.getClass())
		{
			return false;
		}
		final Author other = (Author)obj;
		return Objects.equals(this.addresses, other.addresses) && Objects.equals(this.email, other.email)
			&& Objects.equals(this.firstname, other.firstname)
			&& Objects.equals(this.lastname, other.lastname);
	}
}
