package one.microstream.bsr.domain;

import java.util.List;
import java.util.Objects;

import one.microstream.bsr.dto.PublisherDto;

public class Publisher
{
	private long id;
	private String email;
	private String company;
	private List<Address> addresses;

	public Publisher(final long id, final String email, final String company, final List<Address> addresses)
	{
		this.id = id;
		this.email = email;
		this.company = company;
		this.addresses = addresses;
	}

	public Publisher(final PublisherDto publisher)
	{
		this(
			publisher.id(),
			publisher.email(),
			publisher.company(),
			publisher.addresses().stream().map(Address::new).toList()
		);
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

	public String getCompany()
	{
		return this.company;
	}

	public void setCompany(final String company)
	{
		this.company = company;
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
		return Objects.hash(this.addresses, this.company, this.email, this.id);
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
		final Publisher other = (Publisher)obj;
		return Objects.equals(this.addresses, other.addresses) && Objects.equals(this.company, other.company)
			&& Objects.equals(this.email, other.email)
			&& this.id == other.id;
	}
}
