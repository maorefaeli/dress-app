import React from 'react';
import { useLocale, List, Datagrid, Edit, Create, SimpleForm, NumberField, DateField, FunctionField, UrlField, EmailField, TextField, EditButton, TextInput, DateInput } from 'react-admin';
export const UserList = props => (
    <List {...props}>
        <Datagrid>
            <TextField source="id" label="Id" />
            <EmailField source="username" label="Email"/>
            <FunctionField label="Name" render={record => `${record.firstName || ''} ${record.lastName || ''}`} />
            <TextField source="address" label="Address" />
            <FunctionField label="Rating" render={record => `${record.averageScore} / 5`} />
            <NumberField source="reviewQuantity" label="Review Quantity" />
            <NumberField source="coins" label="Coins" />
        </Datagrid>
    </List>
);