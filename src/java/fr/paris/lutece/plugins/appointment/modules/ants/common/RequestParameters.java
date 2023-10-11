/*
 * Copyright (c) 2002-2023, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.appointment.modules.ants.common;

public class RequestParameters
{

    private String _categoryParameter;
    private String _categoryValue;
    private String _formIdParameter;
    private String _dateTimeParameter;
    private String _dateTimeValue;
    private String _nbPlacesToTakeParameter;
    private String _nbPlacesToTakeValue;
    private String _anchorParameter;
    private String _anchorValue;
    public String getCategoryParameter()
    {
        return _categoryParameter;
    }

    public void setCategoryParameter(String categoryParameter)
    {
        this._categoryParameter = categoryParameter;
    }

    public String getCategoryValue()
    {
        return _categoryValue;
    }

    public void setCategoryValue(String categoryValue)
    {
        this._categoryValue = categoryValue;
    }

    public String getFormIdParameter()
    {
        return _formIdParameter;
    }

    public void setFormIdParameter(String formIdParameter)
    {
        this._formIdParameter = formIdParameter;
    }

    public String getDateTimeParameter()
    {
        return _dateTimeParameter;
    }

    public void setDateTimeParameter(String dateTimeParameter)
    {
        this._dateTimeParameter = dateTimeParameter;
    }

    public String getDateTimeValue()
    {
        return _dateTimeValue;
    }

    public void setDateTimeValue(String dateTimeValue)
    {
        this._dateTimeValue = dateTimeValue;
    }

    public String getNbPlacesToTakeParameter()
    {
        return _nbPlacesToTakeParameter;
    }

    public void setNbPlacesToTakeParameter(String nbPlacesToTakeParameter)
    {
        this._nbPlacesToTakeParameter = nbPlacesToTakeParameter;
    }

    public String getNbPlacesToTakeValue()
    {
        return _nbPlacesToTakeValue;
    }

    public void setNbPlacesToTakeValue(String nbPlacesToTakeValue)
    {
        this._nbPlacesToTakeValue = nbPlacesToTakeValue;
    }

    public String getAnchorParameter()
    {
        return _anchorParameter;
    }

    public void setAnchorParameter(String anchorParameter)
    {
        this._anchorParameter = anchorParameter;
    }

    public String getAnchorValue()
    {
        return _anchorValue;
    }

    public void setAnchorValue(String anchorValue)
    {
        this._anchorValue = anchorValue;
    }

}
