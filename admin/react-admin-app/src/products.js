import React from 'react';
import { useLocale, List, Datagrid, Edit, Create, SimpleForm, NumberField, DateField, FunctionField, UrlField, EmailField, TextField, EditButton, TextInput, DateInput } from 'react-admin';
export const ProductList = props => (
    <List {...props}>
        <Datagrid>
            <TextField source="id" label="Id" />
            <TextField source="name" label="Product" />
            <NumberField source="price" label="Price" />
            <DateField source="fromdate" label="From" locales="he-IL" />
            <DateField source="todate" label="To" locales="he-IL" />
            <FunctionField component="pre" label="Rent Dates" render={record => `${record.rentingDates.map(
                rt => `${new Date(rt.fromdate).toLocaleDateString('he-IL')} - ${new Date(rt.todate).toLocaleDateString('he-IL')}`
            ).join('\n')}`} />
        </Datagrid>
    </List>
);