aya> {2*}:double
{2 *} 
aya> {`.+}:where
{ .+}
aya> {2 double} ~
4 
aya> {2 double} where {, double.`:double}
{2 {2 *}} 
aya> {2 double} where {, double.`:double} ~
2 {2 *} 
aya> {2 double~} where {, double.`:double} ~
4 

Should aoutomatically enclose in paren
aya> {2 double} where {, double.`:double}
{2 ({2 *})}
aya> {2 double} where {, {2*}:double}
{2 ({2 *})}


aya> const [::double] {2 double}
{2 ({2*})}
aya> const [::double] {2 double} ~
4
aya> const [::double] {a double} where {, 2:a}
{2 ({2*})}
