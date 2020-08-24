import React from 'react';
import { useLocale, List, Datagrid, Edit, Create, SimpleForm, NumberField, DateField, FunctionField, UrlField, EmailField, TextField, EditButton, TextInput, DateInput } from 'react-admin';
export const DisputeList = props => (
    <List {...props}>
        <Datagrid>
            <TextField source="id" label="Id" />
            <FunctionField label="Disputer" render={record => `${record.user.firstName || ''} ${record.user.lastName || ''}`} />
            <EmailField source="user.username" label="Disputer Email"/>
            <FunctionField label="Product Owner" render={record => `${record.product.user.firstName || ''} ${record.product.user.lastName || ''}`} />
            <EmailField source="user.username" label="Product Owner Email"/>
            <TextField source="product.id" label="Product Id" />
            <TextField source="product.name" label="Product Name" />
            <DateField source="fromdate" label="From" locales="he-IL" />
            <DateField source="todate" label="To" locales="he-IL" />
        </Datagrid>
    </List>
);