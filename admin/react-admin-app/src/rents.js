import React from 'react';
import { useLocale, List, Datagrid, Edit, Create, SimpleForm, BooleanField, NumberField, DateField, FunctionField, UrlField, EmailField, TextField, EditButton, TextInput, DateInput } from 'react-admin';
export const RentList = props => (
    <List {...props}>
        <Datagrid>
            <TextField source="id" label="Id" />
            <FunctionField label="User" render={record => `${record.user.firstName || ''} ${record.user.lastName || ''}`} />
            <EmailField source="user.username" label="Email"/>
            <TextField source="product.id" label="Product Id" />
            <TextField source="product.name" label="Product Name" />
            <DateField source="fromdate" label="From" locales="he-IL" />
            <DateField source="todate" label="To" locales="he-IL" />
            <BooleanField source="isFinished" label="Is Finished" />
            <NumberField source="coins" label="Coins" />
            <NumberField source="score" label="Score" />
            <BooleanField source="inDispute" label="In Dispute" />
        </Datagrid>
    </List>
);