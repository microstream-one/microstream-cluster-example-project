package one.microstream.bsr.domain;

import java.util.Objects;

import one.microstream.bsr.dto.AddressDto;

public class Address
{
	private String address;
	private String address2;
	private String zip;
	private String city;
	private String country;

	public Address(
		final String address,
		final String address2,
		final String zip,
		final String city,
		final String country
	)
	{
		this.address = address;
		this.address2 = address2;
		this.zip = zip;
		this.city = city;
		this.country = country;
	}

	public Address(final AddressDto address)
	{
		this(address.address(), address.address2(), address.zip(), address.city(), address.country());
	}

	public String getAddress()
	{
		return this.address;
	}

	public void setAddress(final String address)
	{
		this.address = address;
	}

	public String getAddress2()
	{
		return this.address2;
	}

	public void setAddress2(final String address2)
	{
		this.address2 = address2;
	}

	public String getZip()
	{
		return this.zip;
	}

	public void setZip(final String zip)
	{
		this.zip = zip;
	}

	public String getCity()
	{
		return this.city;
	}

	public void setCity(final String city)
	{
		this.city = city;
	}

	public String getCountry()
	{
		return this.country;
	}

	public void setCountry(final String country)
	{
		this.country = country;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.address, this.address2, this.city, this.country, this.zip);
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
		final Address other = (Address)obj;
		return Objects.equals(this.address, other.address) && Objects.equals(this.address2, other.address2)
			&& Objects.equals(this.city, other.city)
			&& Objects.equals(this.country, other.country)
			&& Objects.equals(this.zip, other.zip);
	}
}
